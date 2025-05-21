import { useFormikContext } from "formik";
import { observer } from "mobx-react";
import { memo, useEffect } from "react";
import { pagingShiftWork } from "../../ShiftWork/ShiftWorkService";
import GlobitsPagingAutocompleteV2 from "app/common/form/GlobitsPagingAutocompleteV2";
import { toast } from "react-toastify";
import { useTranslation } from "react-i18next";

function SWSCreateSingleShiftWorkSection(props) {
    const { t } = useTranslation();

    const {
        values,
        setFieldValue
    } = useFormikContext();

    async function fetchDataShiftWork() {
        try {
            const payload = {
                staffId: values?.staff?.id
            };

            const { data } = await pagingShiftWork(payload);

            if (data && data?.content?.length == 1) {
                setFieldValue("shiftWork", data?.content[0]);
            }
        }
        catch (error) {
            console.error(error);
            toast.error("Có lỗi xảy ra khi lấy dữ liệu ca làm việc");
        }
    }

    useEffect(function () {
        if (!values?.staff?.id) return;

        fetchDataShiftWork();
    }, [values?.staff?.id]);

    return (
        <GlobitsPagingAutocompleteV2
            name='shiftWork'
            label={t("staffWorkSchedule.shiftWorks")}
            api={pagingShiftWork}
            required
            disabled={!values?.staff?.id}
            searchObject={{
                staffId: values?.staff?.id
            }}
            getOptionLabel={(option) =>
                option?.name && option?.code
                    ? `${option.name} - ${option.code}`
                    : option?.name || option?.code || ""
            }
        />
    );
}

export default memo(observer(SWSCreateSingleShiftWorkSection));
