export class ProductType {
    id = null;
    code = null;
    name = null;
    description = null;
}

export class Product {
    code = null;
    name = null;
    description = null;
    model = null;
    serialNumber = null;
    manufacturer = null;
    productType = null;
    department = null;
    attributes = [];
    price = null;
}

export class ProductAttribute {
    product = null;
    name = null;
    description = null;
}

export class StaffDocumentItem {
    documentItem = null; // Tài liệu
    isSubmitted = false; // Đã nộp hay chưa
    submissionDate = null; // Ngày nộp
    file = null; // Tài liệu đã được lưu
}

export class RecruitmentRequestItem {
    positionTitle = null;
    inPlanQuantity = null;
    extraQuantity = 0;
    totalQuantity = null;
    announcementQuantity = null;
}

export class HrResourcePlanItemAttribute {
    product = null;
    name = null;
    description = null;
}

export class Asset {
    id = null;
    product = null;
    staff = null;
    startDate = null;
    endDate = null;
    note = null;
}
