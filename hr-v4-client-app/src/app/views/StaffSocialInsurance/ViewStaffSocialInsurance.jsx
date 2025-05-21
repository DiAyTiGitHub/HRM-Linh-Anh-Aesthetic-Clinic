import React, { memo, useEffect, useMemo, useState } from "react";
import { Form, Formik, useFormikContext } from "formik";
import { Grid, Button, DialogContent } from "@material-ui/core";
import { useTranslation } from "react-i18next";
import { observer } from "mobx-react";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import { useStore } from "app/stores";
import BlockIcon from "@material-ui/icons/Block";
import LocalPrintshopIcon from '@material-ui/icons/LocalPrintshop';
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import { formatDate, formatMoney, formatVNDMoney } from "app/LocalFunction";
import Print from "app/common/Component/Print";

function ViewStaffSocialInsurance() {
    const { staffSocialInsuranceStore } = useStore();
    const { selectedStaffSocialInsurance, openViewStaffSocialInsurance, handleClose } = staffSocialInsuranceStore;

    return (
        <GlobitsPopupV2
            scroll={"body"}
            size="md"
            open={openViewStaffSocialInsurance}
            noDialogContent
            title={`Phiếu BHXH nhân viên - ${selectedStaffSocialInsurance.staff.displayName} - ${selectedStaffSocialInsurance.staff.staffCode}`}
            onClosePopup={handleClose}
        >
            <Formik
                enableReinitialize
                initialValues={selectedStaffSocialInsurance}
            >
                <Form className="dialog-body" autoComplete="off" autocomplete="off">
                    <DialogContent className="p-12">

                        <Grid container spacing={2}>
                            <Grid item xs={12} sm={8} lg={9}>
                                <div className="dialogScrollContent pr-12">
                                    <Detail />
                                </div>
                            </Grid>

                            <Grid item xs={12} sm={4} lg={3}>
                                <Information />
                            </Grid>
                        </Grid>
                    </DialogContent>

                    <iframe id="iframeContentPrint" style={{ height: '0px', width: '0px' }} />
                    <StaffSocialInsurancePrint />
                </Form>
            </Formik>
        </GlobitsPopupV2>

    );
}

export default memo(observer(ViewStaffSocialInsurance));

const Detail = memo(() => {
    const { values } = useFormikContext();

    return (
        <section className="verticalTableContainer salaryPayslipPrintSection">
            <div className="vertical-table">
                <table className="print-table" style={{ width: '100%', tableLayout: 'fixed' }}>
                    <tbody>
                        <tr>
                            <th className="p-8" width="50%">Mức lương đóng BHXH</th>
                            <td className="p-8">{formatMoney(values?.insuranceSalary)}</td>
                        </tr>

                        <tr>
                            <th className="p-8" width="50%">Tỷ lệ cá nhân đóng</th>
                            <td className="p-8">{formatVNDMoney(values?.staffPercentage)}%</td>
                        </tr>

                        <tr>
                            <th className="p-8" width="50%">Số tiền cá nhân  đóng</th>
                            <td className="p-8">{formatVNDMoney(values?.staffInsuranceAmount)}</td>
                        </tr>

                        <tr>
                            <th className="p-8" width="50%">Tỷ lệ đơn vị đóng</th>
                            <td className="p-8">{formatVNDMoney(values?.orgPercentage)}%</td>
                        </tr>

                        <tr>
                            <th className="p-8" width="50%">Số tiền đơn vị đóng</th>
                            <td className="p-8">{formatVNDMoney(values?.orgInsuranceAmount)}</td>
                        </tr>

                        <tr>
                            <th className="p-8" width="50%">Tổng tiền</th>
                            <td className="p-8">{formatVNDMoney(values?.totalInsuranceAmount)}</td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </section>
    );
})

const Information = memo(() => {
    const { t } = useTranslation();
    const { staffSocialInsuranceStore } = useStore();
    const { handleClose } = staffSocialInsuranceStore;

    function handlePrintStaffSocialInsurance() {
        const content = document.getElementById("StaffSocialInsurancePrint");
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

    return (
        <Grid container spacing={2}>
            <Grid item xs={12}>
                <GlobitsTextField
                    label={"Phiếu BHXH nhân viên"}
                    name="staff.displayName"
                    required
                    disabled
                />
            </Grid>

            <Grid item xs={12}>
                <GlobitsTextField
                    label={"Kỳ lương"}
                    name="salaryPeriod.name"
                    required
                    disabled
                />
            </Grid>

            <Grid item xs={12}>
                <GlobitsTextField
                    label={"Thuộc bảng lương"}
                    name="salaryResult.name"
                    // required
                    disabled
                />
            </Grid>

            <Grid item xs={12}>
                <div className="pt-12" style={{ color: "#5e6c84" }}>
                    {t("task.action")}
                </div>

                <div className="listButton">
                    <Button
                        variant="contained"
                        className="btn-print"
                        startIcon={<LocalPrintshopIcon />}
                        onClick={handlePrintStaffSocialInsurance}
                    >
                        In phiếu
                    </Button>

                    <Button
                        startIcon={<BlockIcon />}
                        variant="contained"
                        onClick={handleClose}
                    >
                        Đóng
                    </Button>
                </div>
            </Grid>

        </Grid>
    )
})

const StaffSocialInsurancePrint = memo(() => {
    const { values } = useFormikContext();
    const [isFirstLoad, setIsFirstLoad] = useState(true);

    useEffect(() => {
        setIsFirstLoad(false)
    }, [])

    const currentDate = new Date();
    return (
        <Print
            id="StaffSocialInsurancePrint"
            style={`
                    @page { 
                        margin: 7mm;
                    }
                    `
            }
        >
            <section className="px-8 mt-3" style={{ padding: '0 7%' }}>
                <div className="flex justify-between">
                    {useMemo(() => (
                        <div className="text-center">
                            <image src='/assets/images/logo.png' style={{ width: '4.5cm', height: '0.6cm' }} />
                        </div>
                    ), [isFirstLoad])}


                    <div className="text-center">
                        <p className="size-14">
                            <b>XÁC NHẬN ĐÓNG BẢO HIỂM</b>
                        </p>
                        <p className="size-14">
                            <b>Năm {formatDate("YYYY", values?.salaryPeriod?.toDate)}</b>
                        </p>
                    </div>
                </div>

                <p className="mt-3 grid align-start size-13">
                    <span className="col-8">Họ và tên: {values?.staff?.displayName}</span>
                    <span className="col-4">Mã số: {values?.staff?.socialInsuranceNumber}</span>
                </p>

                <p className="mt-2 grid align-start size-13">
                    <span className="col-8">
                        Địa chỉ liên hệ: {values?.staff?.currentResidence}
                    </span>

                    <span className="col-4">
                        Điện thoại: {values?.staff?.phoneNumber}
                    </span>
                </p>
            </section>

            <table className="print-table mt-3">
                <thead>
                    <tr>
                        <th rowSpan={3} style={{ maxWidth: '4mm' }}>Từ tháng năm</th>
                        <th rowSpan={3} style={{ maxWidth: '4mm' }}>Đến tháng năm</th>
                        <th rowSpan={3}>Cấp bậc, vị trí; Chức danh nghề, công việc; Tên đơn vị; Nơi làm việc</th>
                        <th colSpan={5}>
                            Tiền lương tháng/thu nhập đóng
                        </th>
                        <th rowSpan={3}>Tổng</th>
                    </tr>
                    <tr>
                        <th rowSpan={2}>Mức đóng</th>
                        <th colSpan={2}>Tỷ lệ đóng (%)</th>
                        <th colSpan={2}>Số tiền đóng</th>
                    </tr>
                    <tr>
                        <th>Cá nhân</th>
                        <th>Đơn vị</th>
                        <th>Cá nhân</th>
                        <th>Đơn vị</th>
                    </tr>
                </thead>
                <tbody>
                    <tr>
                        <td>{formatDate("MM/YYYY", values?.salaryPeriod?.fromDate)}</td>
                        <td>{formatDate("MM/YYYY", values?.salaryPeriod?.toDate)}</td>
                        <td>Nhân viên, CTy Bestech</td>
                        <td>{formatMoney(values?.insuranceSalary)}</td>
                        <td>{formatVNDMoney(values?.staffPercentage)}%</td>
                        <td>{formatVNDMoney(values?.orgPercentage)}%</td>
                        <td>{formatMoney(values?.staffInsuranceAmount)}</td>
                        <td>{formatMoney(values?.orgInsuranceAmount)}</td>
                        <td>{formatMoney(values?.totalInsuranceAmount)}</td>
                    </tr>
                </tbody>
            </table>

            <div className="flex justify-end mt-5" style={{ padding: '0 7%' }}>
                <div className="text-center">
                    <p className="size-13">Hà Nội, ngày {currentDate.getDate()} tháng {currentDate.getMonth() + 1} năm {currentDate.getFullYear()}</p>
                    <p className="size-14">
                        <b>Giám Đốc</b>
                    </p>
                </div>
            </div>
        </Print>
    )
})