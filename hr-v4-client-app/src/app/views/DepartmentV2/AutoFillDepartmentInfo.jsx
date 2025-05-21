import { Grid } from "@material-ui/core";
import GlobitsPagingAutocomplete from "app/common/form/GlobitsPagingAutocomplete";
import { useFormikContext } from "formik";
import { observer } from "mobx-react";
import { memo, useEffect } from "react";
import { useTranslation } from "react-i18next";
import { toast } from "react-toastify";
import SelectDepartmentComponent from "../../common/SelectComponent/SelectDepartment/SelectDepartmentComponent";
import { pagingDepartmentType } from "../DepartmentType/DepartmentTypeService";
import { checkValidParent } from "../Department/DepartmentService";

function AutoFillDepartmentInfo(props) {
    const { readOnly } = props;
    const { t } = useTranslation();

    const { values, setFieldValue } = useFormikContext();

    const checkValid = async () => {
        try {
            // Skip validation if no parent or no type is selected
            if (!values?.parent?.id || !values?.hrDepartmentType?.id) {
                return true;
            }

            const dto = {
                parentId: values.parent.id,
                typeId: values.hrDepartmentType.id,
            };

            const { data } = await checkValidParent(dto);

            if (data === false) {
                // Reset fields if invalid
                setFieldValue("parent", null);
                setFieldValue("hrDepartmentType", null);

                toast.error(
                    "Phòng ban cha không hợp lệ: Cấp phòng ban cha phải cao hơn hoặc bằng cấp phòng ban hiện tại (sortNumber nhỏ hơn hoặc bằng)"
                );
                return false;
            }

            return true;
        } catch (error) {
            console.error("Validation error:", error);
            toast.error("Có lỗi xảy ra khi kiểm tra phòng ban cha");
            return false;
        }
    };

    useEffect(() => {
        if (values?.hrDepartmentType?.id && values?.parent?.id) {
            checkValid();
        }
    }, [values?.hrDepartmentType, values?.parent]);

    return (
        <>
            <Grid item xs={12} sm={6} md={4} lg={3}>
                <SelectDepartmentComponent
                    name={"parent"}
                    label={t("department.parent")}
                    clearFields={["title"]}
                    disabled={readOnly}
                    disabledTextFieldOnly={true}
                    readOnly={readOnly}
                    placeholder={"Không có phòng ban cha"}
                />
            </Grid>

            <Grid item xs={12} sm={6} md={4} lg={3}>
                <GlobitsPagingAutocomplete
                    name='hrDepartmentType'
                    label={t("department.hrdepartmentType")}
                    api={pagingDepartmentType}
                    getOptionLabel={(option) =>
                        option?.name && option?.sortNumber
                            ? `${option.name} - Cấp ${option.sortNumber}`
                            : option?.name || option?.sortNumber || ""
                    }
                    readOnly={readOnly}
                />
            </Grid>
        </>
    );
}

export default memo(observer(AutoFillDepartmentInfo));
