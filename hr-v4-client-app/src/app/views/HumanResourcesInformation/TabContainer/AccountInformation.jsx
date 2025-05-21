import React, { useEffect } from "react";
import { observer } from "mobx-react";
import "react-toastify/dist/ReactToastify.css";
import {
  Grid,
} from "@material-ui/core";
import { useFormikContext } from "formik";
import { useTranslation } from "react-i18next";
import { useState } from "react";
import GlobitsTextField from "app/common/form/GlobitsTextField";

export default observer(function AccountInformation() {

  const { t } = useTranslation();
  const { values } = useFormikContext();
  const [isCreate, setIsCreate] = useState(false)

  useEffect(() => {
    if (values?.id) {
      setIsCreate(false)
    } else {
      setIsCreate(true)
    }
  }, [values]);
  
  return (
    <Grid item md={12} sm={12} xs={12}>
      <Grid container spacing={2}>
        <Grid item md={6} sm={6} xs={12}>
          <GlobitsTextField
            label={t("humanResourcesInformation.userName")}
            name="username"
            validate
            disabled={!isCreate}
          />
        </Grid> 
        <Grid item md={6} sm={6} xs={12}>
          <GlobitsTextField
            label={t("humanResourcesInformation.email")}
            name="email"
          />
        </Grid>
        
        {isCreate === true &&
          <>
            <Grid item md={6} sm={6} xs={12}>
              <GlobitsTextField
                type="password"
                label={t("user.password")}
                validate
                name="password"
              />
            </Grid>
            <Grid item md={6} sm={6} xs={12}>
              <GlobitsTextField
                type="password"
                label={t("user.rePassword")}
                name="confirmPassword"
                validate
              />
            </Grid>
          </>
        }
      </Grid>
    </Grid>
  );
});
