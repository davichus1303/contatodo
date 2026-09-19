/*
 * Backfills the companyOid of the VichoBox company into every legacy record
 * of the affected modules that still has no company owner.
 *
 * Affected modules: products, acquisitions, expenses, productCostHistory,
 * sales and users.
 *
 * Usage (apply):
 *   mongosh "mongodb://localhost:27017/contatodo" scripts/backfill-vichobox-company-oid.js
 *
 * Preview without writing:
 *   DRY_RUN=true mongosh "mongodb://localhost:27017/contatodo" scripts/backfill-vichobox-company-oid.js
 */
const COMPANY_NAME = "VichoBox";
const COLLECTIONS = ["products", "acquisitions", "expenses", "productCostHistory", "sales", "users"];
const DRY_RUN = process.env.DRY_RUN === "true";

const company = db.companies.findOne({
    name: { $regex: `^${COMPANY_NAME}$`, $options: "i" },
    isDeleted: { $ne: true },
});
if (!company) {
    throw new Error(`Company '${COMPANY_NAME}' was not found`);
}

const companyOid = company._id.toString();
print(`Company '${company.name}' -> ${companyOid}${DRY_RUN ? " (DRY RUN)" : ""}`);

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
