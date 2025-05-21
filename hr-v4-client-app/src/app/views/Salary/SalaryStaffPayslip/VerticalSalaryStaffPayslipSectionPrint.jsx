import React, { memo } from "react";
import { observer } from "mobx-react";
import { formatValue } from "app/LocalFunction";
import Print from "./Print";
// import salaryPrintStyles from './salary-print.scss';

function VerticalSalaryStaffPayslipSectionPrint({ values }) {
    const salaryPrintStyles = `
        @media print {
            body {
                margin: 0;
                padding: 0;
                font-size: 10px;
                line-height: 1.2;
            }

            .footer-section {
                margin-top: 30px;
                font-size: 10px;
                color: #000;
            }
            
            .footer-section .footer-signatures {
                display: flex;
                justify-content: space-between;
                margin-top: 10px;
                width: 100%;
            }
            
            .footer-section .p-left,
            .footer-section .p-right {
                margin: 0;
                line-height: 1.2;
                text-align: center;
                width: 50%;
            }
            
            .footer-section .date-text {
                text-align: right;
                margin-top: 10px;
                font-style: italic;
                color: #555;
            }
            
            .vertical-table {
                margin: 0 auto;
                width: 100%;
                font-family: Arial, sans-serif;
            }
            
            .print-table {
                width: 100%; /* Ensures the table takes the full width */
                border-collapse: collapse;
                margin-bottom: 10px;
            }
            
            .print-table table {
                margin-top: 10px;
                width: 100%; /* Ensures individual tables also take the full width */
                border-collapse: collapse;
            }

            .print-table th.column-header {
                width: 50%;
                background-color: #f5f5f5;
                font-weight: bold;
                text-align: left;
                padding: 4px;
                border: 1px solid #ccc;
            }
            
            .print-table td.column-data {
                padding: 4px;
                border: 1px solid #ccc;
                text-align: left;
            }
            
            .print-table tbody tr:nth-child(even) {
                background-color: #f9f9f9;
            }
            
            h1, h2 {
                font-family: "Arial Black", Arial, sans-serif;
                margin: 0;
                color: #333;
            }
            
            h1 {
                font-size: 14px;
                margin-bottom: 5px;
                text-transform: uppercase;
            }
            
            h2 {
                font-size: 16px;
                color: #007bff;
                margin-bottom: 10px;
            }
            
            p {
                margin: 3px 0;
                font-size: 10px;
            }
        }
    `;
    const salaryItems = values?.salaryResultStaffItems || [];

    let listA = [];
    let listB = []; // Các khoản giảm trừ
    let listC = []; /// các khoản thu nhập
    let listD = [];
    let listE = [];
    let listF = [];

    let listAdding = [];
    let listDed = [];
    // Hàm để bổ sung các item rỗng nếu danh sách chưa đủ 10 phần tử
    const fillEmptyItems = (list, maxLength) => {
        while (list.length < maxLength) {
            list.push({ salaryResultItem: { displayName: '\u00A0' }, value: " " }); // Item rỗng
        }
        return list;
    };

    salaryItems.forEach((item) => {
        if (item.referenceCode?.startsWith("BH_") ||
            item.referenceCode?.startsWith("TAM_UNG_SYSTEM") ||
            item.referenceCode === "THUE_TNCN" ||
            item.referenceCode === "TONG_TIEN_BAO_HIEM_CONG_TY_DONG" ||
            item.referenceCode === "TONG_TIEN_BH_NHAN_VIEN_DONG" ||
            item.referenceCode === "GIAM_TRU_GIA_CANH_SYSTEM" ||
            item.referenceCode === "LUONG_DONG_BHXH_SYSTEM" ||
            item.referenceCode === "KHOAN_PHI_CONG_DOAN_CONG_TY_DONG_SYSTEM") {
            listB.push(item);
        } else if (item.referenceCode?.startsWith("SO_NGAY_CONG_")) {
            listF.push(item);
        } else if (item.referenceCode === "HO_VA_TEN_NV_SYSTEM") {
            listA.push(item);
        } else if (item.referenceCode === "STT_SYSTEM") {

        } else if (item.referenceCode === "THUC_NHAN") {
            listE.push(item);
        } else if (item.referenceCode?.startsWith("LUONG_") ||
            item.referenceCode === "THUONG_HIEU_XUAT_CONG_VIEC" ||
            item.referenceCode?.startsWith("PHU_CAP_")) {
            listC.push(item);
        }

        if(item?.salaryResultItem?.salaryItem?.type === 1) {
            listAdding.push(item);
        }
        if(item?.salaryResultItem?.salaryItem?.type === 2) {
            listDed.push(item);
        }

    });

    console.log(listAdding);
    console.log(listDed);
    const sortByDisplayOrder = (list) => {
        return list.sort((a, b) => a.referenceDisplayOrder - b.referenceDisplayOrder);
    };

    listA = sortByDisplayOrder(listA);
    listB = sortByDisplayOrder(listB);
    listC = sortByDisplayOrder(listC);
    listD = sortByDisplayOrder(listD);
    listE = sortByDisplayOrder(listE);
    listF = sortByDisplayOrder(listF);

    listAdding = sortByDisplayOrder(listAdding);
    listDed = sortByDisplayOrder(listDed);

    return (
        <Print id="salaryPayslipPrintSectionPrint">
            <style>{salaryPrintStyles}</style>
            {/* in dọc */}
            <div className="vertical-table">
                <div style={{ textAlign: 'left', fontSize: '13px', marginBottom: '10px' }}>
                    <h2 style={{ margin: 0 }}>
                        BESTECH
                    </h2>
                </div>
                <div style={{ textAlign: 'center', fontSize: '15px', marginBottom: '10px' }}>
                    <h1 style={{ textAlign: 'center', margin: 0 }}>
                        Phiếu lương {values?.salaryPeriod?.name || ""}
                    </h1>
                    <p style={{ textAlign: 'right', margin: 0 }}>
                        Mã nhân viên {values?.staff?.staffCode || "......"} - Vị trí {values?.staff?.currentPosition?.name || "......"}
                    </p>
                </div>


                <div style={{ display: 'flex', alignItems: 'stretch' }}>

                    <div className="print-table">
                        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '20px' }}>
                            <table>
                                <tbody>
                                    {listA.map((item, index) => {
                                        let displayValue = formatValue(item?.value, item?.valueType);
                                        return (
                                            <tr key={`listA-row-${index}`}>
                                                <th className="column-header">
                                                    {item?.salaryResultItem?.displayName}
                                                </th>
                                                <td className="column-data">
                                                    {displayValue}
                                                </td>
                                            </tr>
                                        );
                                    })}
                                </tbody>
                            </table>
                            <table>
                                <tbody>
                                    {listF.map((item, index) => {
                                        let displayValue = formatValue(item?.value, item?.valueType);
                                        return (
                                            <tr key={`listA-row-${index}`}>
                                                <th className="column-header">
                                                    {item?.salaryResultItem?.displayName}
                                                </th>
                                                <td className="column-data">
                                                    {displayValue}
                                                </td>
                                            </tr>
                                        );
                                    })}
                                </tbody>
                            </table>
                        </div>
                        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '20px' }}>
                            <table >
                                <thead>
                                    <tr>
                                        <th className="column-header">
                                            (I) Các khoản thu nhập
                                        </th>
                                        <th className="column-header">

                                        </th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {listC.map((item, index) => {
                                        let displayValue = formatValue(item?.value, item?.valueType);
                                        return (
                                            <tr key={`listC-row-${index}`}>
                                                <td className="column-data">
                                                    {item?.salaryResultItem?.displayName}
                                                </td>
                                                <td className="column-data">
                                                    {displayValue}
                                                </td>
                                            </tr>
                                        );
                                    })}
                                </tbody>
                            </table>


                            <table>
                                <thead>
                                    <tr>
                                        <th className="column-header">
                                            (III) Các khoản giảm trừ
                                        </th>
                                        <th className="column-header"></th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {listB.map((item, index) => {
                                        let displayValue = formatValue(item?.value, item?.valueType);
                                        return (
                                            <tr key={`listB-row-${index}`}>
                                                <td className="column-data">{item?.salaryResultItem?.displayName}</td>
                                                <td className="column-data">{displayValue}</td>
                                            </tr>
                                        );
                                    })}
                                </tbody>
                            </table>
                        </div>

                        <table>
                            <thead>
                                <tr>
                                    <th className="column-header">
                                        (IV). Thực nhận
                                    </th>
                                    <th className="column-header">

                                    </th>
                                </tr>
                            </thead>
                            <tbody>
                                {listE.map((item, index) => {
                                    let displayValue = formatValue(item?.value, item?.valueType);
                                    return (
                                        <tr key={`listD-row-${index}`}>
                                            <td className="column-data">
                                                {item?.salaryResultItem?.displayName}
                                            </td>
                                            <td className="column-data">
                                                {displayValue}
                                            </td>
                                        </tr>
                                    );
                                })}
                            </tbody>
                        </table>

                    </div>

                </div>


                <div className="footer-section">
                    <p className="date-text">
                        ................, ngày ....... tháng ...... năm 20.......
                    </p>
                    <div className="footer-signatures">
                        <div className="p-left">
                            <p>Người lập phiếu</p>
                            <p>(Ký và ghi rõ họ tên)</p>
                        </div>
                        <div className="p-right">
                            <p>Giám đốc</p>
                            <p>(Ký và ghi rõ họ tên)</p>
                        </div>
                    </div>
                </div>
            </div>
        </Print>
    );
}


export default memo(observer(VerticalSalaryStaffPayslipSectionPrint));
