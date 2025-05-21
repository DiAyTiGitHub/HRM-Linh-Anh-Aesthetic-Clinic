import { Grid, makeStyles, DialogContent, DialogActions, ButtonGroup } from "@material-ui/core";
import TabAccordion from "app/common/Accordion/TabAccordion";
import GlobitsTable from "app/common/GlobitsTable";
import { getDate } from "app/LocalFunction";
import { FieldArray, useFormikContext, Formik, Form } from "formik";
import React, { memo, useMemo, useState, useEffect } from "react";
import { useTranslation } from "react-i18next";
import "react-toastify/dist/ReactToastify.css";
import { Button } from "@material-ui/core";
import AddIcon from "@material-ui/icons/Add";
import localStorageService from "app/services/localStorageService";
import { useParams } from "react-router";

// pop up
import GlobitsDateTimePicker from "app/common/form/GlobitsDateTimePicker";
import SaveIcon from "@material-ui/icons/Save";
import BlockIcon from "@material-ui/icons/Block";
import * as Yup from "yup";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import { pagingAllowance } from "app/views/Allowance/AllowanceService";
import GlobitsPagingAutocompleteV2 from "app/common/form/GlobitsPagingAutocompleteV2";
import moment from "moment";
import { useStore } from "app/stores";

const useStyles = makeStyles((theme) => ({
    root: {
        "& .MuiAccordion-rounded": { borderRadius: "5px" },
        "& .MuiPaper-root": { borderRadius: "5px" },
        "& .MuiAccordionSummary-root": {
            borderRadius: "5px",
            color: "#5899d1",
            fontWeight: "400",
            "& .MuiTypography-root": { fontSize: "1rem" },
        },
        "& .Mui-expanded": {
            "& .MuiAccordionSummary-root": {
                backgroundColor: "#EBF3F9",
                color: "#5899d1",
                fontWeight: "700",
                maxHeight: "50px !important",
                minHeight: "50px !important",
            },
            "& .MuiTypography-root": { fontWeight: 700 },
        },
        "& .MuiButton-root": { borderRadius: "0.125rem !important" },
    },
    noAllowance: {
        textAlign: "center",
        marginTop: theme.spacing(4),
        fontStyle: "italic",
        color: "#999",
    },
    buttonGroupSpacing: {
        marginBottom: "10px",
    },
}));

function StaffAllowance() {
    const { t } = useTranslation();
    const classes = useStyles();
    const { values } = useFormikContext();
    const { staffStore, staffAllowanceStore } = useStore();
    const { id } = useParams();

    const {
        saveStaffAllowance,
        selectedStaffAllowance,
        getListStaffAllowanceByStaffId,
        listStaffAllowance,
        resetStore,
    } = staffAllowanceStore;

    const [groupedData, setGroupedData] = useState(null);
    const [hasAllowances, setHasAllowances] = useState(false);
    const [isLoading, setIsLoading] = useState(true);

    useEffect(() => {
        if (id) {
            setIsLoading(true); // Bắt đầu tải dữ liệu
            getListStaffAllowanceByStaffId(id).finally(() => {
                setIsLoading(false); // Hoàn thành tải
            });
        }
    }, [id]);

    useEffect(() => {
        if (listStaffAllowance && listStaffAllowance.length > 0) {
            const grouped = listStaffAllowance.reduce((acc, item) => {
                const allowanceId = item?.allowance?.id || "unclassified";
                if (!acc[allowanceId]) {
                    acc[allowanceId] = { name: item?.allowance?.name || "Chưa phân loại", data: [] };
                }
                acc[allowanceId].data.push(item);
                return acc;
            }, {});

            Object.keys(grouped).forEach((key) => {
                grouped[key].data.sort((a, b) => {
                    return new Date(b.startDate) - new Date(a.startDate); // Sắp xếp giảm dần
                });
            });

            setGroupedData(grouped);
            setHasAllowances(true);
        } else {
            setGroupedData(null);
            setHasAllowances(false);
        }
    }, [listStaffAllowance]);

    async function handleStaffAllowanceForm(staffAllowanceForm) {
        await saveStaffAllowance(staffAllowanceForm);
        setIsLoading(true);
        await getListStaffAllowanceByStaffId(values?.id);
        setIsLoading(false);
    }

    const columns = [
        {
            title: t("Ngày bắt đầu"),
            field: "startDate",
            width: "40%",
            render: (row) => <span>{getDate(row?.startDate)}</span>,
            align: "center",
        },
        {
            title: t("Ngày kết thúc"),
            field: "endDate",
            width: "40%",
            render: (row) => <span>{getDate(row?.endDate)}</span>,
            align: "center",
        },
        {
            title: t("Thuộc chính sách"),
            field: "allowancePolicy",
            render: (data) => <span>{data?.allowancePolicy?.name}</span>,
            align: "center",
        },
        {
            title: t("Công thức/Giá trị tính toán"),
            field: "usingFormula",
            render: (data) => (
                <span>
                    {typeof data?.usingFormula === "number"
                        ? new Intl.NumberFormat("vi-VN", {
                              style: "currency",
                              currency: "VND",
                          }).format(data.usingFormula)
                        : data?.usingFormula}
                </span>
            ),
            align: "center",
        },
    ];

    // ✅ Nhóm dữ liệu theo allowance.id
    // const groupedData = values?.staffAllowance?.reduce((acc, item) => {
    //   const allowanceId = item?.allowance?.id || "unclassified";
    //   if (!acc[allowanceId]) {
    //     acc[allowanceId] = { name: item?.allowance?.name || "Chưa phân loại", data: [] };
    //   }
    //   acc[allowanceId].data.push(item);
    //   return acc;
    // }, {});

    // const hasAllowances = groupedData && Object.keys(groupedData).length > 0;

    const isAdmin = useMemo(() => {
        let roles = localStorageService.getLoginUser()?.user?.roles?.map((item) => item.authority) || [];
        let auth = ["HR_MANAGER", "ROLE_ADMIN", "ROLE_SUPER_ADMIN"];
        return roles.some((role) => auth.indexOf(role) !== -1);
    }, []);

    const [selectedEdit, setSelectedEdit] = useState(null);
    const [selectedDelete, setSelectedDelete] = useState(null);
    const [allowance, setAllowance] = useState(null);

    const [initialValues, setInitialValues] = useState(selectedStaffAllowance);

    useEffect(
        function () {
            setInitialValues({
                ...selectedStaffAllowance,
                staff: { id: id },
            });
        },
        [selectedStaffAllowance, selectedStaffAllowance?.id]
    );

    return (
        <Grid container spacing={2}>
            {isAdmin && values?.id && (
                <Grid item xs={12} md={6}>
                    <ButtonGroup
                        color='container'
                        aria-label='outlined primary button group'
                        className={classes.buttonGroupSpacing}>
                        <Button startIcon={<AddIcon />} type='button' onClick={() => setAllowance(initialValues)}>
                            Thêm mới
                        </Button>
                    </ButtonGroup>
                </Grid>
            )}
            <Grid item xs={12}>
                {isLoading ? (
                    <p className='w-100 text-center'>Đang tải dữ liệu...</p>
                ) : hasAllowances ? (
                    Object.entries(groupedData).map(([allowanceId, { name, data }]) => (
                        <TabAccordion key={allowanceId} title={name}>
                            <GlobitsTable
                                data={data}
                                columns={columns}
                                maxWidth='100%'
                                nonePagination
                                selection={false}
                            />
                        </TabAccordion>
                    ))
                ) : (
                    <p className='w-100 text-center'>Chưa có phụ cấp</p>
                )}
            </Grid>

            <StaffAllowanceForm
                allowance={allowance}
                onClosePopup={() => setAllowance(null)}
                onSaveAllowance={handleStaffAllowanceForm}
            />
        </Grid>
    );
}

export default memo(StaffAllowance);

export const StaffAllowanceForm = memo(({ allowance, onClosePopup, onSaveAllowance, readonly }) => {
    const { t } = useTranslation();

    const validationSchema = Yup.object({
        allowance: Yup.object().required(t("validation.required")).nullable(),
        startDate: Yup.date()
            .test("is-greater", "Ngày bắt đầu phải lớn thiết lập", function (value) {
                const { signedDate } = this.parent;
                if (signedDate && value) {
                    return moment(value).isAfter(moment(signedDate), "date");
                }
                return true;
            })
            .transform(function transformDate(castValue, originalValue) {
                return originalValue ? new Date(originalValue) : castValue;
            })
            .required(t("validation.required"))
            .typeError("Ngày bắt đầu không đúng định dạng")
            .nullable(),

        endDate: Yup.date()
            .test("is-greater", "Ngày kết thúc phải lớn ngày bắt đầu", function (value) {
                const { startDate } = this.parent;
                if (startDate && value) {
                    return moment(value).isAfter(moment(startDate), "date");
                }
                return true;
            })
            .transform(function transformDate(castValue, originalValue) {
                return originalValue ? new Date(originalValue) : castValue;
            })
            // .required(t("validation.required"))
            .typeError("Ngày kết thúc không đúng định dạng")
            .nullable(),
    });

    return (
        <GlobitsPopupV2
            size='md'
            //scroll={"body"}
            open={Boolean(allowance)}
            noDialogContent
            title={t("Thêm phụ cấp")}
            onClosePopup={onClosePopup}
            popupId={"popupAllowance"}>
            <Formik
                enableReinitialize
                initialValues={allowance}
                onSubmit={(values) => {
                    onSaveAllowance(values);
                    onClosePopup();
                }}
                validationSchema={validationSchema}>
                {({ isSubmitting, values, setFieldValue, initialValues }) => {
                    return (
                        <Form autoComplete='off'>
                            <DialogContent className='dialog-body p-12'>
                                <Grid container spacing={2}>
                                    {/* <Grid item xs={12} sm={6} md={4}>
                </Grid> */}
                                    <Grid item xs={12} sm={6} md={4}>
                                        <GlobitsPagingAutocompleteV2
                                            required
                                            name='allowance'
                                            label={t("Phụ cấp")}
                                            api={pagingAllowance}
                                            disabled={allowance?.allowancePolicy?.id}
                                        />
                                    </Grid>

                                    <Grid item sm={6} xs={12} md={4}>
                                        <GlobitsDateTimePicker
                                            label={"Ngày bắt đầu"}
                                            name='startDate'
                                            disabled={values?.allowancePolicy?.id}
                                            required
                                        />
                                    </Grid>

                                    <Grid item sm={6} xs={12} md={4}>
                                        <GlobitsDateTimePicker
                                            label={"Ngày kết thúc"}
                                            name='endDate'
                                            disabled={values?.allowancePolicy?.id}
                                        />
                                    </Grid>

                                    <Grid item xs={12}>
                                        <GlobitsTextField
                                            label='Công thức/Giá trị tính toán'
                                            name='usingFormula'
                                            multiline
                                            rows={2}
                                            disabled={values?.allowancePolicy?.id}
                                        />
                                    </Grid>
                                </Grid>
                            </DialogContent>

                            <DialogActions className='p-15 dialog-footer'>
                                <div className='flex flex-space-between flex-middle'>
                                    <Button
                                        startIcon={<BlockIcon />}
                                        variant='contained'
                                        className='mr-12 btn btn-secondary d-inline-flex'
                                        color='secondary'
                                        onClick={() => onClosePopup()}>
                                        {readonly ? "Đóng" : t("general.button.cancel")}
                                    </Button>
                                    {!readonly && (
                                        <Button
                                            startIcon={<SaveIcon />}
                                            className='mr-0 btn btn-primary d-inline-flex'
                                            variant='contained'
                                            color='primary'
                                            type='submit'>
                                            {t("general.button.save")}
                                        </Button>
                                    )}
                                </div>
                            </DialogActions>
                        </Form>
                    );
                }}
            </Formik>
        </GlobitsPopupV2>
    );
});
