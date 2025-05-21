import React, { memo } from "react";
import { useFormikContext } from "formik";
import { Grid, Button } from "@material-ui/core";
import { useTranslation } from "react-i18next";
import { observer } from "mobx-react";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import GlobitsSelectInput from "app/common/form/GlobitsSelectInput";
import LocalConstants from "app/LocalConstants";
import SaveIcon from "@material-ui/icons/Save";
import { useStore } from "app/stores";
import BlockIcon from "@material-ui/icons/Block";
import ReplayIcon from "@material-ui/icons/Replay";
import LocalPrintshopIcon from "@material-ui/icons/LocalPrintshop";
import VerticalSalaryStaffPayslipSectionPrint from "./VerticalSalaryStaffPayslipSectionPrint";
import { pagingStaff } from "app/views/HumanResourcesInformation/StaffService";
import GlobitsPagingAutocompleteV2 from "app/common/form/GlobitsPagingAutocompleteV2";

function SalaryStaffPayslipUpdateSection() {
    const { values, isSubmitting } = useFormikContext();

    const { salaryStaffPayslipStore, hrRoleUtilsStore } = useStore();
    const { t } = useTranslation();
    const { handleClose, openSelectSignature, handleClickPrint } = salaryStaffPayslipStore;

    function handlePrintPaySlip() {
        const content = document.getElementById("salaryPayslipPrintSectionPrint");
        const iframe = document.getElementById("iframeContentPrint");

        // Ẩn nội dung form in trước khi in
        content.style.display = "none";

        const printWindow = iframe.contentWindow;

        printWindow.document.open();
        printWindow.document.write(content.innerHTML);
        printWindow.document.close();
        printWindow.focus();

        // Hiển thị popup in
        printWindow.print();

        // Hiển thị lại nội dung form in sau khi in xong
        window.onafterprint = () => {
            content.style.display = "block";
        };
    }

    const handleClickIconPrint = () => {
        handleClickPrint();
    };

    const { hasShiftAssignmentPermission, checkHasShiftAssignmentPermission, isManager, isAdmin } = hrRoleUtilsStore;

    // function handlePrintPaySlip() {
    //     const printContent = document.querySelector(".salaryPayslipPrintSection"); // Select the section
    //     if (printContent) {
    //         const printWindow = window.open("", "_blank"); // Open a new window for printing

    //         // Gather all stylesheets and inline styles
    //         const styles = Array.from(document.querySelectorAll("style, link[rel='stylesheet']"))
    //             .map(style => style.outerHTML)
    //             .join("\n");

    //         // Write to the new window
    //         printWindow.document.write(`
    //             <html>
    //                 <head>
    //                     <title>Phiếu lương nhân viên ${values?.staff?.displayName || ""}</title>
    //                     ${styles} <!-- Include all existing styles -->
    //                     <style>
    //                         .print-table {
    //                             width: 100%;
    //                             border-collapse: collapse;
    //                             font-family: "Times New Roman", serif;
    //                             font-size: 12px;
    //                             table-layout: fixed;
    //                         }
    //                         .print-table th,
    //                         .print-table td {
    //                             border: 1px solid #000;
    //                             padding: 8px;
    //                             text-align: left;
    //                             vertical-align: middle;
    //                             width: 50%; /* Cột cân bằng */
    //                         }

    //                         .print-table th {
    //                             background-color: #f4f4f4;
    //                             font-weight: bold;
    //                             text-align: left;
    //                         }

    //                         .print-table td {
    //                             text-align: left;
    //                         }

    //                         @media print {
    //                             .verticalTableContainer {
    //                                 page-break-inside: avoid;
    //                             }
    //                             .print-table th,
    //                             .print-table td {
    //                                 border: 1px solid #000;
    //                             }
    //                         }
    //                     </style>
    //                 </head>
    //                 <body>
    //                     ${printContent.outerHTML} <!-- Include the content -->
    //                 </body>
    //             </html>
    //         `);

    //         // Finalize and trigger print
    //         printWindow.document.close();
    //         printWindow.focus(); // Focus the print window
    //         printWindow.print(); // Trigger the print dialog
    //         // printWindow.close(); // Close the window after printing
    //     } else {
    //         console.error("Print content not found");
    //     }
    // }

    return (
        <>
            <Grid container spacing={2}>
                <Grid item xs={12}>
                    <GlobitsPagingAutocompleteV2
                        name='staff'
                        label={"Phiếu lương nhân viên"}
                        api={pagingStaff}
                        getOptionLabel={(option) =>
                            option?.displayName && option?.staffCode
                                ? `${option.displayName} - ${option.staffCode}`
                                : option?.displayName || option?.staffCode || ""
                        }
                        readOnly
                    // onChange={(value) => handleFilter({ ...searchObject, staffId: value?.id })}
                    />
                </Grid>

                <Grid item xs={12}>
                    <GlobitsTextField
                        label={"Kỳ lương"}
                        name='salaryPeriod.name'
                        required
                        readOnly
                    />
                </Grid>

                <Grid item xs={12}>
                    <GlobitsTextField
                        label={"Thuộc bảng lương"}
                        name='salaryResult.name'
                        // required
                        readOnly
                    />
                </Grid>

                <Grid item xs={12}>
                    <GlobitsTextField
                        label='Ghi chú'
                        name='note'
                        multiline
                        rows={3}
                        disabled={!isAdmin}
                    />
                </Grid>

                {/* <Grid item xs={12}>
                    <GlobitsSelectInput
                        label={"Trạng thái phê duyệt"}
                        name='approvalStatus'
                        hideNullOption
                        options={LocalConstants.SalaryStaffPayslipApprovalStatus.getListData()}
                        disabled={!isAdmin}
                    />
                </Grid> */}

                <Grid item xs={12}>
                    <GlobitsSelectInput
                        label={"Trạng thái chi trả"}
                        name='paidStatus'
                        hideNullOption
                        options={LocalConstants.StaffPayslipsPaidStatus.getListData()}
                        readOnly={!isAdmin}
                    />
                </Grid>

                <Grid item xs={12}>
                    <div className='pt-12' style={{ color: "#5e6c84" }}>
                        {t("task.action")}
                    </div>

                    <div className='listButton'>
                        {isManager ||
                            (isAdmin && (
                                <Button
                                    variant='contained'
                                    className='btn-print'
                                    startIcon={<LocalPrintshopIcon />}
                                    disabled={isSubmitting}
                                    onClick={handleClickIconPrint}>
                                    In phiếu
                                </Button>
                            ))}
                        {isAdmin && (
                            <Button
                                variant='contained'
                                className='btn-green'
                                startIcon={<SaveIcon />}
                                type='submit'
                                disabled={isSubmitting}>
                                Cập nhật
                            </Button>
                        )}

                        {isAdmin && (
                            <Button
                                variant='contained'
                                className='btn-info'
                                startIcon={<ReplayIcon />}
                                type='reset'
                                disabled={isSubmitting}>
                                Hoàn tác
                            </Button>
                        )}

                        <Button
                            startIcon={<BlockIcon />}
                            variant='contained'
                            onClick={handleClose}
                            disabled={isSubmitting}>
                            Đóng
                        </Button>
                    </div>
                </Grid>
            </Grid>

            {/* Để dọc */}
            <iframe id='iframeContentPrint' style={{ height: "0px", width: "0px" }}></iframe>

            {/* Để ngang */}
            {/* <iframe
                id="iframeContentPrint"
                style={{
                    height: "0",
                    width: "0",
                    border: "none",
                    display: "none",
                }}
            ></iframe> */}
            {/* <VerticalSalaryStaffPayslipSectionPrint
                values={values}
                // style={{ display: "none" }} // để ngang
            /> */}
        </>
    );
}

export default memo(observer(SalaryStaffPayslipUpdateSection));
