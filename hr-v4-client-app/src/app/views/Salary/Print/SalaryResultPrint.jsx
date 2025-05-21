import GlobitsTable from "app/common/GlobitsTable";
import { useStore } from "app/stores";
import { observer } from "mobx-react";
import { forwardRef, memo } from "react";
import PrintCommon from "../../../common/Print/PrintCommon";
import { formatMoney } from "../../../LocalFunction";

function SalaryResultPrint({ printData, componentRef }, ref) {
    // Lấy tất cả cột từ printData (giả sử printData.columns chứa danh sách cột)

    const { salaryResultStore } = useStore();

    const { selectedColumns, allColumns } = salaryResultStore;
    // Lọc các cột dựa trên selectedColumns
    // Lọc các cột được chọn, nhưng loại bỏ index, staffName, signature vì chúng sẽ được thêm thủ công
    const filteredColumns = allColumns
        .filter(
            (col) =>
                selectedColumns.includes(col.code) &&
                col.code !== "index" &&
                col.code !== "staffName" &&
                col.code !== "signature"
        )
        .map((col) => ({
            title: col.displayName,
            field: col.code,
            align: col.align || "left",
            width: col.width || undefined,
        }));

    // Tạo danh sách cột cố định: index và staffName ở đầu, signature ở cuối
    const columns = [
        { title: "STT", field: "index", align: "center", width: 20 }, // Cột STT luôn ở đầu
        { title: "Mã nhân viên", field: "staffCode", align: "center" },
        { title: "Họ và tên", field: "staffName", align: "center" }, // Cột Họ và tên luôn tiếp theo
        ...filteredColumns, // Các cột được chọn
        { title: "Ký nhận", field: "signature", align: "center" }, // Cột Ký nhận luôn ở cuối
    ];
    // Dữ liệu bảng
    const rows =
        printData?.salaryResultStaffs?.map((item, index) => {
            // Tạo object chứa dữ liệu từ salaryResultStaffItems
            const staffData = item?.salaryResultStaffItems?.reduce((acc, staffItem) => {
                acc[staffItem.referenceCode] = staffItem.value;
                return acc;
            }, {});

            // Tạo object rowData chỉ chứa các cột được chọn
            let rowData = {};

            // Duyệt qua selectedColumns để chỉ lấy dữ liệu của các cột được chọn
            selectedColumns.forEach((colCode) => {
                rowData[colCode] = staffData[colCode] ? formatMoney(staffData[colCode]) : "N/A";
            });
            
            rowData = {
              ...rowData,
              index : index + 1,
              staffCode: item?.staffCode,
              staffName: item?.staffName
            }
            return rowData;
        }) || [];
    return (
        <PrintCommon ref={componentRef} marginPage='8mm' size='A4' orientation="landscape">
            <div>
                <div style={{ textAlign: "center", fontWeight: "bold", fontSize: "18px" }}>CÔNG TY TNHH GLOBITS</div>
                <div style={{ textAlign: "center", fontWeight: "bold", fontSize: "20px", textTransform: "uppercase" }}>
                    BẢNG THANH TOÁN TIỀN LƯƠNG
                </div>
                <div style={{ textAlign: "center", fontSize: "16px", marginBottom: "16px" }}>
                    {printData?.name || ""}
                </div>

                {/* Hiển thị bảng chỉ với các cột đã chọn */}
                <GlobitsTable columns={columns} data={rows} nonePagination={true} />
            </div>
        </PrintCommon>
    );
}

export default memo(observer(forwardRef(SalaryResultPrint)));
