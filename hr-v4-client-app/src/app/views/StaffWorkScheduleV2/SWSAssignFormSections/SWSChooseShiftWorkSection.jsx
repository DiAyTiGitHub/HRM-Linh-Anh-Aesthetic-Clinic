import { Grid, Tooltip } from "@material-ui/core";
import GlobitsCheckBox from "app/common/form/GlobitsCheckBox";
import GlobitsPagingAutocomplete from "app/common/form/GlobitsPagingAutocomplete";
import GlobitsVNDCurrencyInput from "app/common/form/GlobitsVNDCurrencyInput";
import LocalConstants from "app/LocalConstants";
import { observer } from "mobx-react";
import { memo, useEffect } from "react";
import { useTranslation } from "react-i18next";
import GlobitsSelectInput from "app/common/form/GlobitsSelectInput";
import GlobitsPagingAutocompleteV2 from "app/common/form/GlobitsPagingAutocompleteV2";
import GlobitsAsyncAutocomplete from "app/common/form/GlobitsAsyncAutocomplete";
import { useFormikContext } from "formik";
import { pagingAllDepartments } from "app/views/Department/DepartmentService";
import { pagingAllOrg } from "app/views/Organization/OrganizationService";
import { pagingShiftWork } from "app/views/ShiftWork/ShiftWorkService";
import { pagingPositionTitle } from "app/views/PositionTitle/PositionTitleService";
import { toast } from "react-toastify";

function SWSChooseShiftWorkSection(props) {
    const { t } = useTranslation();

    const { values, setFieldValue } = useFormikContext();

    async function fetchDataShiftWork() {
        try {
            const payload = {
                departmentId: values?.department?.id,
            };

            const { data } = await pagingShiftWork(payload);

            if (data && data?.content?.length == 1) {
                setFieldValue("shiftWorks", [data?.content[0]]);
            }
        } catch (error) {
            console.error(error);
            toast.error("Có lỗi xảy ra khi lấy dữ liệu ca làm việc");
        }
    }

    useEffect(
        function () {
            setFieldValue("shiftWorks", []);

            if (!values?.department?.id) {
                return;
            }

            fetchDataShiftWork();
        },
        [values?.department?.id]
    );

    return (
        <Grid container spacing={2}>
            <Grid item xs={12} sm={6}>
                <GlobitsPagingAutocompleteV2
                    name='shiftWorks'
                    label={
                        <span>
                            {t("staffWorkSchedule.shiftWorks")}
                            <span style={{ color: "red" }}> *</span>
                        </span>
                    }
                    api={pagingShiftWork}
                    searchObject={{
                        pageIndex: 1,
                        pageSize: 9999,
                        departmentId: values?.department?.id,
                    }}
                    getOptionLabel={(option) =>
                        option?.name && option?.code
                            ? `${option.name} - ${option.code}`
                            : option?.name || option?.code || ""
                    }
                    multiple
                />
            </Grid>

            <Grid item xs={12} sm={6}>
                <Tooltip
                    placement='top'
                    arrow
                    title='Chấm công chỉ yêu cầu nhân viên có 1 lần Checkin vào đầu buổi và 1 lần Checkout'>
                    <GlobitsCheckBox label={"Chỉ chấm công vào ra 1 lần"} name='allowOneEntryOnly' />
                </Tooltip>
            </Grid>

            {values?.allowOneEntryOnly && (
                <Grid item xs={12} sm={6}>
                    <GlobitsSelectInput
                        label='Cách tính thời gian'
                        hideNullOption
                        name='timekeepingCalculationType'
                        keyValue='value'
                        options={LocalConstants.TimekeepingCalculationType.getListData()}
                    />
                </Grid>
            )}

            <Grid item xs={12} sm={6}>
                <Tooltip
                    placement='top'
                    arrow
                    title='Kết quả chấm công của nhân viên đối với ca làm việc cần sự xác nhận của người quản lý để dữ liệu được sử dụng cho tính công, tính lương'>
                    <GlobitsCheckBox label={"Cần xác nhận của người quản lý"} name='needManagerApproval' />
                </Tooltip>
            </Grid>
        </Grid>
    );
}

export default memo(observer(SWSChooseShiftWorkSection));
