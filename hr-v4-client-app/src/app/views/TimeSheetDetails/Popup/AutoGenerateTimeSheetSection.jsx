import React, { memo, useState, useEffect } from "react";
import { Icon, IconButton, Tooltip } from "@material-ui/core";
import { useTranslation } from "react-i18next";
import { useFormikContext } from "formik";
import { useStore } from "app/stores";
import { observer } from "mobx-react";
import Popover from '@material-ui/core/Popover';
import { Button, Grid } from '@material-ui/core';
import SlowMotionVideoIcon from '@material-ui/icons/SlowMotionVideo';
import GlobitsDateTimePicker from "app/common/form/GlobitsDateTimePicker";
import { Form, Formik } from "formik";
import moment from "moment";
import * as Yup from "yup";
import { useParams } from "react-router";
import { toast } from "react-toastify";

function AutoGenerateTimeSheetSection(props) {
    const {

    } = props;

    const { t } = useTranslation();

    const { timeSheetDetailsStore, timeSheetStore, } = useStore();

    const {
        currentStaff,
        autoGenerateTimeSheetDetails
    } = timeSheetDetailsStore;

    const [openPopover, setOpenPopover] = useState(false);

    function handleClosePopover() {
        setOpenPopover(null);
    }

    useEffect(function () {
        if (openPopover) {

        }
    }, [openPopover]);

    const validationSchema = Yup.object({
        fromDate: Yup.date()
            .transform(function transformDate(castValue, originalValue) {
                return originalValue ? new Date(originalValue) : castValue;
            })
            .required(t("validation.required"))
            .typeError("Ngày bắt đầu không đúng định dạng")
            .nullable(),

        toDate: Yup.date()
            .test(
                "is-greater",
                "Ngày kết thúc phải lớn ngày bắt đầu",
                function (value) {
                    const { fromDate } = this.parent;
                    if (fromDate && value) {
                        return moment(value).isSameOrAfter(moment(fromDate), "date");
                    }
                    return true;
                }
            )
            .transform(function transformDate(castValue, originalValue) {
                return originalValue ? new Date(originalValue) : castValue;
            })
            .required(t("validation.required"))
            .typeError("Ngày kết thúc không đúng định dạng")
            .nullable(),

    });

    const { id: currentStaffId } = useParams();

    async function handleGenerateTSD(values) {
        //console.log("on generate values: ", values);
        await autoGenerateTimeSheetDetails(values);
        // toast.info("Tính năng đang được phát triển");
        handleClosePopover();
    }

    return (
        <>
            <div className="flex align-center">
                <p className="m-0 timeSheetDetailScreenTitle pr-12">
                    Nhật kí công việc: {(currentStaff && currentStaff?.name)}
                </p>

                {/* Disable function auto generatetimesheet */}
                <Button
                    className="btn autoGenerateTimesheetBtn d-inline-flex "
                    startIcon={<SlowMotionVideoIcon />}
                    onClick={(event) => setOpenPopover(event?.currentTarget)}
                >
                    Tự động tạo nhật kí
                </Button>
            </div>

            {
                Boolean(openPopover) && (
                    <Popover
                        anchorEl={openPopover}
                        open={Boolean(openPopover)}
                        onClose={handleClosePopover}
                        anchorOrigin={{
                            vertical: 'bottom',
                            horizontal: 'center',
                        }}
                        transformOrigin={{
                            vertical: 'top',
                            horizontal: 'center',
                        }}
                    >
                        <Formik
                            enableReinitialize
                            initialValues={{
                                staffId: currentStaffId,
                                fromDate: null,
                                toDate: null
                            }}
                            validationSchema={validationSchema}
                            onSubmit={handleGenerateTSD}
                        >
                            {({ setFieldValue }) => (
                                <Form autoComplete="off" className="">
                                    <PopoverContent
                                        handleClosePopover={handleClosePopover}
                                    />
                                </Form>
                            )}
                        </Formik>


                    </Popover>
                )
            }

        </>
    );
}

export default memo(observer(AutoGenerateTimeSheetSection));



function PopoverContent(props) {
    const {
        handleClosePopover
    } = props;

    return (
        <div className="p-8 autoGenerateTSDPopover">
            <div className="pb-4" style={{ position: 'relative', borderBottom: "1px solid #dee2e6" }}>
                <p className="flex flex-center m-0 ">Lựa chọn khoảng thời gian</p>

                <IconButton
                    className="p-0"
                    style={{ position: "absolute", right: "0", top: "0" }}
                    onClick={handleClosePopover}
                >
                    <Icon color="disabled" title={"Đóng"} fontSize="small" >
                        close
                    </Icon>
                </IconButton>
            </div>

            <Grid container spacing={2} className="" >
                <Grid item xs={12}>
                    <GlobitsDateTimePicker
                        label="Ngày bắt đầu:"
                        name="fromDate"
                        disableFuture={true}
                    />
                </Grid>

                <Grid item xs={12} style={{ borderBottom: "1px solid #dee2e6" }}>
                    <GlobitsDateTimePicker
                        label="Ngày kết thúc:"
                        name="toDate"
                        disableFuture={true}
                    />
                </Grid>

                <Grid item xs={12}>
                    <p className="m-0 popoverNote " style={{ textWrap: "pretty" }}>
                        (*) Lưu ý: Các nhật kí cũ trong khoảng thời gian được chọn sẽ bị xóa. Nhật kí công việc sẽ được tự động tạo cho các buổi làm việc đã được điểm danh, các thông tin: thời gian bắt đầu, kết thúc, người phụ trách của phần việc hoặc hạng mục công việc con trong phần việc ĐÃ ĐƯỢC ĐIỀN ĐẦY ĐỦ.
                    </p>
                </Grid>
            </Grid>

            <Grid className="mt-6" container spacing={1}>
                <Grid item xs={12}>
                    <Button
                        startIcon={<SlowMotionVideoIcon />}
                        fullWidth
                        className='text-white bgc-lighter-dark-blue'
                        type="submit"
                    >
                        Tự động tạo nhật kí
                    </Button>
                </Grid>
            </Grid>

        </div>
    );
}