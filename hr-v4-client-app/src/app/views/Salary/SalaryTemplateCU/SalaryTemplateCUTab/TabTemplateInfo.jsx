import { Grid } from "@material-ui/core";
import GlobitsImageUpload from "app/common/form/FileUpload/GlobitsImageUpload";
import GlobitsCheckBox from "app/common/form/GlobitsCheckBox";
import GlobitsDateTimePicker from "app/common/form/GlobitsDateTimePicker";
import GlobitsNumberInput from "app/common/form/GlobitsNumberInput";
import GlobitsPagingAutocomplete from "app/common/form/GlobitsPagingAutocomplete";
import GlobitsPagingAutocompleteV2 from "app/common/form/GlobitsPagingAutocompleteV2";
import GlobitsSelectInput from "app/common/form/GlobitsSelectInput";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import LocalConstants from "app/LocalConstants";
import { pagingAdministratives } from "app/views/AdministrativeUnit/AdministrativeUnitService";
import { pagingCountry } from "app/views/Country/CountryService";
import { pagingEthnicities } from "app/views/Ethnics/EthnicsService";
import { pagingReligions } from "app/views/Religion/ReligionService";
import { Field, useFormikContext } from "formik";
import { observer } from "mobx-react";
import React, { memo } from "react";
import { useTranslation } from "react-i18next";
import SalaryTemplateItemList from "../SalaryTemplateItem/SalaryTemplateItemList";
import SalaryTemplateItemForm from "../SalaryTemplateItem/SalaryTemplateItemForm";
import { useStore } from "../../../../stores";

function TabTemplateInfo() {
    const { salaryTemplateItemStore, salaryTemplateStore } = useStore();

    const {
        shouldOpenEditorDialog
    } = salaryTemplateItemStore;

    const { openViewPopup: readOnly } = salaryTemplateStore;

    const { values, setFieldValue } = useFormikContext();
    const { t } = useTranslation();

    return (
        <Grid container spacing={2}>
            <Grid item xs={12} sm={6} md={4}>
                <GlobitsTextField
                    validate
                    label="Mã mẫu"
                    name="code"
                    readOnly={readOnly}
                />
            </Grid>

            <Grid item xs={12} sm={6} md={4}>
                <GlobitsTextField
                    validate
                    label="Tên mẫu bảng lương"
                    name="name"
                    readOnly={readOnly}
                />
            </Grid>

            {/* <Grid item xs={12} sm={6} md={4} className="pt-30 pl-10" >
                <GlobitsCheckBox
                    label={t("Đang được sử dụng")}
                    name="isActive"
                />
            </Grid> */}

            <Grid item xs={12} sm={6} md={4} className="pt-30 pl-10" >
                <GlobitsCheckBox
                    label={t("Có tạo phiếu lương")}
                    name="isCreatePayslip"
                />
            </Grid>


            <Grid item xs={12}>
                <GlobitsTextField
                    label="Mô tả"
                    name="description"
                    multiline
                    rows={3}
                    readOnly={readOnly}
                />
            </Grid>

            {/* <Grid item xs={12}>
                <SalaryTemplateItemList />
            </Grid> */}

        </Grid>
    );
}

export default memo(observer(TabTemplateInfo));

