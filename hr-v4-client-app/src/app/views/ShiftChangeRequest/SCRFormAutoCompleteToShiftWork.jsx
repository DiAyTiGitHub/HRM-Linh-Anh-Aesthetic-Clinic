import GlobitsPagingAutocompleteV2 from "app/common/form/GlobitsPagingAutocompleteV2";
import LocalConstants from "app/LocalConstants";
import { getIn, useFormikContext } from "formik";
import { observer } from "mobx-react";
import moment from "moment";
import { memo, useEffect } from "react";
import { useTranslation } from "react-i18next";
import { pagingShiftWork } from "../ShiftWork/ShiftWorkService";
import { toast } from "react-toastify";

function SCRFormAutoCompleteToShiftWork(props) {
    const { t } = useTranslation();

    const {
        readOnly
    } = props;

    const {
        values,
        setFieldValue
    } = useFormikContext();

    async function fetchDataShiftWork() {
        try {
            const payload = {
                staffId: values?.registerStaff?.id
            };

            const { data } = await pagingShiftWork(payload);

            if (data && data?.content?.length == 1) {
                setFieldValue("toShiftWork", data?.content[0]);
            }
        }
        catch (error) {
            console.error(error);
            toast.error("Có lỗi xảy ra khi lấy dữ liệu ca làm việc");
        }
    }

    useEffect(function () {
        if (!values?.registerStaff?.id) return;

        fetchDataShiftWork();
    }, [values?.registerStaff?.id]);

    return (
        <GlobitsPagingAutocompleteV2
            name='toShiftWork'
            label={"Ca làm việc được yêu cầu đổi"}
            api={pagingShiftWork}
            required
            readOnly={
                readOnly || values?.approvalStatus ===
                LocalConstants.ShiftChangeRequestApprovalStatus.APPROVED.value
            }
            getOptionDisabled={(option) => {
                const fromShiftWork = getIn(values, "fromShiftWork");
                const fromWorkingDate = getIn(values, "fromWorkingDate");
                const toWorkingDate = getIn(values, "toWorkingDate");

                const isSameShift = fromShiftWork?.id === option?.id;
                const isSameDate =
                    fromWorkingDate &&
                    toWorkingDate &&
                    moment(fromWorkingDate).isSame(moment(toWorkingDate), "date");

                // Nếu cùng ngày + cùng ca thì disable
                return isSameShift && isSameDate;
            }}
            searchObject={{
                staffId: values?.registerStaff?.id
            }}
            getOptionLabel={(option) =>
                option?.name && option?.code
                    ? `${option.name} - ${option.code}`
                    : option?.name || option?.code || ""
            }
        />
    );
}

export default memo(observer(SCRFormAutoCompleteToShiftWork));
