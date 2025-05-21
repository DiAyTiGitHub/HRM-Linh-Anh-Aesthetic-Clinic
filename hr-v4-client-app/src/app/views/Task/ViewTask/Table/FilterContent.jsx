import React, { useEffect, memo } from "react";
import { useStore } from "app/stores";
import { observer } from "mobx-react";
import { useTranslation } from "react-i18next";
import { 
    Grid,
} from "@material-ui/core";
import { Form, useFormikContext } from "formik";
import LocalConstants from "app/LocalConstants";
import GlobitsSelectInput from "app/common/form/GlobitsSelectInput";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import GlobitsAutocomplete from "app/common/form/GlobitsAutocomplete";

function FilterContent(props) {
    const { t } = useTranslation();

    const { taskStore, staffStore, workingStatusStore } = useStore();
    const { onWorkingStaffList } = staffStore;
    const { listWorkingStatus } = workingStatusStore;
    const { handleChangeTaskTableFilter } = taskStore;

    const { values } = useFormikContext();

    useEffect(function () {
        handleChangeTaskTableFilter(values);
    }, [values]);

    return (
        <Form autoComplete="off">
            <Grid container spacing={2}>
                {/* <Grid item xs={12} sm={6} md={3}>
                        <GlobitsPagingAutocomplete
                            label={t("timeSheet.employee")}
                            name="staffId"
                            searchObject={{projectId: currentProject?.id}}
                            api={pagingStaff}
                            displayData="displayName"
                        />
                    </Grid> */}

                <Grid item xs={12} sm={6} md={3}>
                    {/* <GlobitsSelectInput
                        label={t("timeSheet.employee")}
                        name="staffId"
                        keyValue="id"
                        displayvalue={"displayName"}
                        options={onWorkingStaffList}
                    /> */}

                    <GlobitsAutocomplete
                        name="staff"
                        label={t("timeSheet.employee")}
                        className="w-100"
                        options={onWorkingStaffList}
                        displayData='displayName'
                    />
                </Grid>



                <Grid item xs={12} sm={6} md={3}>
                    <GlobitsSelectInput
                        label={t("timeSheet.priority")}
                        name="priority"
                        keyValue="id"
                        options={LocalConstants.Priority}
                    />
                </Grid>

                <Grid item xs={12} sm={6} md={3}>
                    <GlobitsSelectInput
                        label={t("timeSheet.workingStatus")}
                        name="workingStatusId"
                        keyValue="id"
                        options={listWorkingStatus}
                    />
                </Grid>

                <Grid item xs={12} sm={6} md={3}>
                    <GlobitsTextField
                        label={t("general.enterSearch")}
                        name="keyword"
                    />
                </Grid>

                {/* <Grid item xs={12} sm={6} md={3} className='flex-center flex flex-end'>
                        <Button
                        color=""
                            className="btn d-inline-flex btn-secondary "
                            startIcon={<SearchIcon />}
                            onClick={() => console.log("searching...")}
                            fullWidth
                        >
                            Tìm kiếm
                        </Button>
                    </Grid> */}

            </Grid>
        </Form>
    );
}

export default memo(observer(FilterContent));