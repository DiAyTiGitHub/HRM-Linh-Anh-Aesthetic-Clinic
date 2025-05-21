import React, { useEffect, useState } from "react";
import {
    Grid,
    DialogActions,
    Button,
    DialogContent,
} from "@material-ui/core";
import { Formik, Form, useFormikContext } from "formik";
import { useTranslation } from "react-i18next";
import GlobitsDateTimePicker from "app/common/form/GlobitsDateTimePicker";
import GlobitsPagingAutocomplete from "app/common/form/GlobitsPagingAutocomplete";
import GlobitsCheckBox from "app/common/form/GlobitsCheckBox";
import * as Yup from "yup";
import moment from "moment";
import GlobitsPopup from "app/common/GlobitsPopup";
import DepartmentFilters from "app/views/Department/DepartmentFilters";
import SelectDepartmentListInStaff from "../../Component/SelectDepartmentListInStaff";
import { pagingPosition } from "app/views/Position/PositionService";
import GlobitsSelectInput from "app/common/form/GlobitsSelectInput";
import LocalConstants from "app/LocalConstants";
import ChooseUsingStaffSection from "app/views/User/UsingAccountStaff/ChooseUsingStaffSection";
import ChooseSingleDepartmentSection from "app/views/Department/ChooseSingleDepartment/ChooseSingleDepartmentSection";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";

export default function PositionStaffPopup(props) {
    const { t } = useTranslation();
    const { open, handleClose, item, handleSubmit, editable } = props;
    const { values } = useFormikContext();

    const initialItem = {
        position: null,
        department: null,
        fromDate: new Date(),
        toDate: null,
        supervisor: null
    };

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
                    const { startDate } = this.parent;
                    if (startDate && value) {
                        return moment(value).isAfter(moment(startDate), "date");
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
        position: Yup.object().required(t("validation.required")).nullable(),
    });

    const [formValues, setFormValues] = useState(null);

    useEffect(() => {
        if (item) {
            setFormValues({ ...item });
        } else {
            setFormValues({ ...initialItem });
        }
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [item]);

    return (
        <GlobitsPopupV2
            size={"sm"}
            open={open}
            onClosePopup={handleClose}
            noDialogContent
            title={
                <span>
                    {editable ? t("general.button.add") : t("general.button.edit")}{" "}
                    {t("positionStaff.title")}
                </span>
            }
        >
            <Formik
                initialValues={formValues}
                onSubmit={(values) => handleSubmit(values)}
                validationSchema={validationSchema}
            >
                {({ isSubmitting, values }) => (
                    <Form autoComplete="off">
                        <DialogContent
                            className="dialog-body o-hidden p-12"
                            // style={{ maxHeight: "80vh", minWidth: "300px" }}
                        >
                            <Grid container spacing={2}>
                                <Grid item xs={12} sm={6}>
                                    <GlobitsPagingAutocomplete
                                        validate
                                        required
                                        name="position"
                                        label={t("positionStaff.position")}
                                        api={pagingPosition}
                                    />
                                </Grid>
                                <Grid item xs={12} sm={6}>
                                    <GlobitsSelectInput
                                        required
                                        hideNullOption
                                        label={t("positionStaff.relationshipType")}
                                        name="relationshipType"
                                        options={LocalConstants.RelationshipType.getListData()}
                                    />
                                </Grid>
                                <Grid item xs={12} sm={6}>
                                    <ChooseSingleDepartmentSection
                                        label={"Phòng ban làm việc"}
                                        name="hrDepartment"
                                    />
                                </Grid>
                                <Grid item xs={12} sm={6} className="pt-28 pl-20" >
                                    <GlobitsCheckBox
                                        label='Là vị trí chính'
                                        name='mainPosition'
                                        disabled={!values?.hrDepartment}
                                    />
                                </Grid>
                                <Grid item xs={12} sm={6}>
                                    <ChooseUsingStaffSection
                                        label={"Người quản lý"}
                                        name="supervisor"
                                    />
                                </Grid>
                                <Grid item xs={6}>
                                    <GlobitsDateTimePicker
                                        required
                                        label={t("positionStaff.fromDate")}
                                        name="fromDate"
                                    />
                                </Grid>
                                <Grid item xs={6}>
                                    <GlobitsDateTimePicker
                                        required
                                        label={t("positionStaff.toDate")}
                                        name="toDate"
                                    />
                                </Grid>
                            </Grid>
                        </DialogContent>

                        <DialogActions className="dialog-footer px-12">
                            <div className="flex flex-space-between flex-middle">
                                <Button
                                    className="mr-12 btn btn-secondary d-inline-flex"
                                    onClick={() => {
                                        handleClose();
                                    }}
                                >
                                    {t("general.button.close")}
                                </Button>
                                <Button
                                    className="mr-0 btn btn-primary d-inline-flex"
                                    type="submit"
                                    disabled={isSubmitting}
                                >
                                    {t("general.button.save")}
                                </Button>
                            </div>
                        </DialogActions>
                    </Form>
                )}
            </Formik>
        </GlobitsPopupV2>
    );
}
