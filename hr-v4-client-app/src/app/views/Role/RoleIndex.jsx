import { observer } from "mobx-react";
import React, { useEffect } from "react";
import { useStore } from "../../stores";
import RoleList from "./RoleList";
import { useTranslation } from "react-i18next";
import { Grid } from "@material-ui/core";
import GlobitsBreadcrumb from "../../common/GlobitsBreadcrumb";

export default observer(function RoleIndex() {
  const { roleStore } = useStore();
  const { t } = useTranslation();

  const { updatePageData } = roleStore;

  useEffect(() => {
    updatePageData();
  }, [updatePageData]);
  return (
    <div className="content-index">
      <div className='index-breadcrumb py-6'>
        <GlobitsBreadcrumb routeSegments={
            [
                {name: t("navigation.administration.title")},
                {name: t("role.title")}
            ]
        }/>
      </div>
      <Grid className="index-card" container spacing={3}>
        <Grid item xs={12}>
          <RoleList />
        </Grid>
      </Grid>
    </div>
  );
});
