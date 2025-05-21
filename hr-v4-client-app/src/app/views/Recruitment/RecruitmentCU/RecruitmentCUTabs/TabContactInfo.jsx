import { Grid } from '@material-ui/core';
import GlobitsAsyncAutocomplete from 'app/common/form/GlobitsAsyncAutocomplete';
import GlobitsTextField from 'app/common/form/GlobitsTextField';
import { useFormikContext } from 'formik';
import React from 'react'
import { useTranslation } from 'react-i18next';
import { pagingStaff } from 'app/views/HumanResourcesInformation/StaffService';
import { observer } from 'mobx-react';
import GlobitsNumberInput from 'app/common/form/GlobitsNumberInput';

const Tab2 = () => {
    const { t } = useTranslation();

    const { values } = useFormikContext();

  

    return (
        <Grid item xs={12}>
            <Grid container spacing={2}>
                <Grid item xs={12} sm={6} md={4} lg={3}>
                    <GlobitsAsyncAutocomplete
                        label={"Nhân viên liên hệ"}
                        validate
                        name="contactStaff"
                        api={pagingStaff}
                        searchObject={{ pageIndex: 1, pageSize: 999, keyword: "" }}
                        displayData='displayName'
                    />
                </Grid>

                {/* <Grid item xs={12} sm={6} md={4} lg={3}>
                    <GlobitsAsyncAutocomplete
                        label={"Vị trí công tác"}
                        validate
                        name="positionCS"
                        api={pagingPosition}
                        searchObject={{ pageIndex: 1, pageSize: 9999, keyword: "", departmentId: values?.hrDepartmentCS?.id }}
                    />
                </Grid> */}

                <Grid item xs={12} sm={6} md={4} lg={3}>
                    <GlobitsNumberInput
                        label={"Điện thoại liên lạc"}
                        name="phoneNumber"
                    />
                </Grid>
                <Grid item xs={12} sm={6} md={4} lg={3}>
                    <GlobitsNumberInput
                        label={"Điện thoại đơn vị"}
                        name="officePhoneNumber"
                    />
                </Grid>
                <Grid item xs={12} sm={6} md={4} lg={3}>
                    <GlobitsTextField
                        type="email"
                        label={"Email"}
                        name="contactEmail"
                    />
                </Grid>
                <Grid item xs={12} sm={6} md={4} lg={3}>
                    <GlobitsTextField
                        type="text"
                        label={"Website"}
                        name="contactWebsite"
                    />
                </Grid>
            </Grid>
        </Grid>
    )
}

export default observer(Tab2) 
