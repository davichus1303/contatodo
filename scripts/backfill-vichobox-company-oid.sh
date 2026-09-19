#!/usr/bin/env bash
#
# Backfills the companyOid of the VichoBox company into every legacy record
# of the affected modules that still has no company owner.
#
# Affected modules: products, acquisitions, expenses, productCostHistory,
# sales and users.
#
# Usage (apply):
#   scripts/backfill-vichobox-company-oid.sh
#
# Preview without writing:
#   DRY_RUN=true scripts/backfill-vichobox-company-oid.sh
#
# Overridable environment variables (use the BACKFILL_ prefixed ones to avoid
# clashing with the backend's MONGO_URI / MONGO_DATABASE):
#   BACKFILL_MONGO_URI  Mongo connection string including the database
#                       (default: mongodb://localhost:27017/contatodo)
#   BACKFILL_COMPANY_NAME  Company to resolve (default: VichoBox)
#   BACKFILL_DRY_RUN    true to only report (default: false)
set -euo pipefail

MONGO_URI="${BACKFILL_MONGO_URI:-mongodb://localhost:27017/contatodo}"
COMPANY_NAME="${BACKFILL_COMPANY_NAME:-VichoBox}"
DRY_RUN="${BACKFILL_DRY_RUN:-false}"

export BACKFILL_COMPANY_NAME="$COMPANY_NAME"
export BACKFILL_DRY_RUN="$DRY_RUN"

mongosh "$MONGO_URI" --quiet --eval '
const COMPANY_NAME = process.env.BACKFILL_COMPANY_NAME;
const COLLECTIONS = ["products", "acquisitions", "expenses", "productCostHistory", "sales", "users"];
const DRY_RUN = process.env.BACKFILL_DRY_RUN === "true";

const company = db.companies.findOne({
    name: { $regex: `^${COMPANY_NAME}$`, $options: "i" },
    isDeleted: { $ne: true },
});
if (!company) {
    throw new Error(`Company ${COMPANY_NAME} was not found`);
}

const companyOid = company._id.toString();
print(`Company ${company.name} -> ${companyOid}${DRY_RUN ? " (DRY RUN)" : ""}`);

const missingCompany = {
    $or: [
        { companyOid: { $exists: false } },
        { companyOid: null },
        { companyOid: "" },
    ],
};

COLLECTIONS.forEach((name) => {
    const collection = db.getCollection(name);
    const pending = collection.countDocuments(missingCompany);
    let updated = 0;
    if (!DRY_RUN && pending > 0) {
        updated = collection.updateMany(missingCompany, { $set: { companyOid: companyOid } }).modifiedCount;
    }
    print(`  ${name}: ${pending} record(s) without companyOid${DRY_RUN ? "" : `, ${updated} updated`}`);
});
'
