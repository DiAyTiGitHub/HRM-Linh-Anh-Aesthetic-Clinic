import { Grid } from "@material-ui/core";
import { useFormikContext } from "formik";
import { observer } from "mobx-react";
import React, { memo } from "react";
import GlobitsPagingAutocompleteV2 from "app/common/form/GlobitsPagingAutocompleteV2";
import { pagingStaff } from "../HumanResourcesInformation/StaffService";
import GlobitsSelectInput from "app/common/form/GlobitsSelectInput";
import LocalConstants from "app/LocalConstants";
import { useTranslation } from "react-i18next";

function HrResourcePlanApproveInfo(props) {
    const {
        readOnly
    } = props;

    const {
        values,
        setFieldValue
    } = useFormikContext();

    const { t } = useTranslation();


    return (
        <Grid container spacing={2}>
            <Grid item xs={12}>
                <p className='m-0 p-0 borderThrough2'>Thông tin phê duyệt</p>
            </Grid>

            <Grid item xs={12}>
                <GlobitsPagingAutocompleteV2
                    name='requester'
                    label={t("Người tạo yêu cầu")}
                    api={pagingStaff}
                    readOnly={readOnly}
                    getOptionLabel={(option) => {
                        if (!option) return "";

                        const name = option.displayName || "";
                        const code = option.staffCode ? ` - ${option.staffCode}` : "";
                        const position = option.currentPosition?.name ? ` (${option.currentPosition.name})` : "";

                        return `${name}${code}${position}`;
                    }}
                />
            </Grid>

            <Grid item xs={12} sm={6}>
                <GlobitsPagingAutocompleteV2
                    name='viceGeneralDirector'
                    label={t("Phó tổng giám đốc")}
                    api={pagingStaff}
                    readOnly={readOnly}
                    getOptionLabel={(option) => {
                        if (!option) return "";

                        const name = option.displayName || "";
                        const code = option.staffCode ? ` - ${option.staffCode}` : "";
                        const position = option.currentPosition?.name ? ` (${option.currentPosition.name})` : "";

                        return `${name}${code}${position}`;
                    }}
                />
            </Grid>

            <Grid item xs={12} sm={6}>
                <GlobitsSelectInput
                    hideNullOption
                    label='Trạng thái phê duyệt'
                    name='viceGeneralDirectorStatus'
                    keyValue='value'
                    readOnly
                    options={LocalConstants.HrResourcePlanApprovalStatus.getListData()}
                />
            </Grid>

            <Grid item xs={12} sm={6}>
                <GlobitsPagingAutocompleteV2
                    name='generalDirector'
                    label={t("Tổng giám đốc")}
                    api={pagingStaff}
                    readOnly={readOnly}
                    getOptionLabel={(option) => {
                        if (!option) return "";

                        const name = option.displayName || "";
                        const code = option.staffCode ? ` - ${option.staffCode}` : "";
                        const position = option.currentPosition?.name ? ` (${option.currentPosition.name})` : "";

                        return `${name}${code}${position}`;
                    }}
                />
            </Grid>

            <Grid item xs={12} sm={6}>
                <GlobitsSelectInput
                    hideNullOption
                    label='Trạng thái phê duyệt'
                    name='generalDirectorStatus'
                    keyValue='value'
                    readOnly
                    options={LocalConstants.HrResourcePlanApprovalStatus.getListData()}
                />
            </Grid>
        </Grid>
    );
}

export default memo(observer(HrResourcePlanApproveInfo));
