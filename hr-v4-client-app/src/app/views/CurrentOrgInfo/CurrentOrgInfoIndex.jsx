import React, { useState, useEffect, memo, useMemo } from "react";
import { Formik, Form } from "formik";
import { Grid, DialogActions, Button, DialogContent } from "@material-ui/core";
import { useTranslation } from "react-i18next";
import { useStore } from "../../stores";
import { observer } from "mobx-react";
import EditIcon from "@material-ui/icons/Edit";
import moment from "moment";
import GlobitsBreadcrumb from "app/common/GlobitsBreadcrumb";
import WorkplaceSection from "./WorkplaceSection";
import OrgInfoSection from "./OrgInfoSection";
import UpdateOrgInfoSection from "./UpdateOrgInfoSection";
import CancelOutlinedIcon from '@material-ui/icons/Close';

function CurrentOrgInfoIndex() {
    const { t } = useTranslation();

    const { organizationStore } = useStore();
    const {
        getCurrentOrganizationOfCurrentUser,
        resetStore,
        canCreateUpdateDelete,
        selectedOrganization
    } = organizationStore;

    useEffect(function () {
        async function getCompanyInfo() {
            await getCurrentOrganizationOfCurrentUser();
        }

        getCompanyInfo();

        return resetStore;
    }, []);

    const [isUpdateCurrentOrg, setIsUpdateCurrentOrg] = useState(false);

    const isManager = useMemo(function () {
        return canCreateUpdateDelete();
    }, [selectedOrganization?.id]);

    function handleCancelUpdate() {
        setIsUpdateCurrentOrg(false);
    }

    return (
        <div className="content-index">
            <div className="index-breadcrumb">
                <GlobitsBreadcrumb
                    routeSegments={[{ name: "Cơ cấu tổ chức" }, { name: "Thông tin công ty" }]}
                />
            </div>
            <div className="">
                <Grid container spacing={2}>
                    {isManager && !isUpdateCurrentOrg && (
                        <Grid item xs={12} className="flex flex-end">
                            <Button
                                className="btn bgc-lighter-dark-green d-inline-flex text-white"
                                variant="contained"
                                onClick={() => setIsUpdateCurrentOrg(true)}
                            >
                                <EditIcon /> Cập nhật thông tin công ty
                            </Button>
                        </Grid>
                    )}

                    {isManager && isUpdateCurrentOrg && (
                        <Grid item xs={12} className="flex flex-end">
                            <Button
                                className="btn btn-danger d-inline-flex"
                                variant="contained"
                                onClick={handleCancelUpdate}
                            >
                                <CancelOutlinedIcon /> Hủy cập nhật
                            </Button>
                        </Grid>
                    )}

                    {isUpdateCurrentOrg && (
                        <Grid item xs={12} sm={4} lg={3}>
                            <UpdateOrgInfoSection
                                handleCancelUpdate={handleCancelUpdate}
                            />
                        </Grid>
                    )}

                    {!isUpdateCurrentOrg && (
                        <Grid item xs={12} sm={4} lg={3}>
                            <OrgInfoSection />
                        </Grid>
                    )}

                    {/* <UserInOrgSection
                                userOrgs={selectedOrganization?.users}
                            /> */}

                    <Grid item xs={12} sm={8} lg={9}>
                        <WorkplaceSection
                            isUpdateCurrentOrg={isUpdateCurrentOrg}
                            isManager={isManager}
                        />
                    </Grid>

                </Grid>
            </div>

        </div>
    );
}

export default memo(observer(CurrentOrgInfoIndex));

