import React, { memo, useEffect, useState } from "react";
import { Formik, Form } from "formik";
import {
  Grid,
  DialogActions,
  Button,
  DialogContent,
  useMediaQuery,
  useTheme,
  ButtonGroup,
} from "@material-ui/core";
import { useTranslation } from "react-i18next";
import { useStore } from "../../stores";
import * as Yup from "yup";
import { observer } from "mobx-react";
import GlobitsTextField from "../../common/form/GlobitsTextField";
import GlobitsBreadcrumb from "app/common/GlobitsBreadcrumb";
import ClearIcon from "@material-ui/icons/Clear";
import DescriptionIcon from "@material-ui/icons/Description";
import history from "history.js";
import ConstantList from "../../appConfig";

function RewardForm() {
  const { t } = useTranslation();
  const theme = useTheme();
  const [anchorEl, setAnchorEl] = React.useState(null);
  const urlParams = new URLSearchParams(window.location.search);
  const idReward = urlParams.get("id");
  const open = Boolean(anchorEl);
  const id = open ? "simple-popper" : undefined;
  const handleClick = (event) => {
    setAnchorEl(anchorEl ? null : event.currentTarget);
  };

  const { selectedExamCategory, getExamCategory ,saveOrUpdate} = useStore().rewardStore;

  const isExtraSmall = useMediaQuery((theme) => theme.breakpoints.down("xs"));
  const isMobile = useMediaQuery(theme.breakpoints.down("sm"));

  useEffect(() => {
    if (idReward) {
      getExamCategory();
    }
  }, []);


  const validationSchema = Yup.object({
    code:Yup.string().required("Mời bạn nhập mã").nullable(),
    name:Yup.string().required("Mời bạn nhập tên").nullable(),
    languageKey:Yup.string().required("Mời bạn nhập ngôn ngữ").nullable(),
    rewardType:Yup.string().required("Mời bạn nhập loại khen thưởng").nullable(),
    formal:Yup.string().required("Mời bạn nhập hình thức").nullable(),
    evaluateYear:Yup.string().required("Mời bạn nhập đánh giá năm").nullable(),
    evaluateLevel:Yup.string().required("Mời bạn nhập đánh giá mức độ").nullable(),
  })

  return (
    <Formik
      initialValues={selectedExamCategory}
      onSubmit={saveOrUpdate}
      enableReinitialize
      validationSchema={validationSchema}
    >
      <Form>
        <div className="content-index">
          <GlobitsBreadcrumb
            routeSegments={[
              { name: t("navigation.category.title") },
              { name: t("navigation.category.staff.title") },
              { name: t("navigation.category.staff.awardType") },
            ]}
          />
          <Grid container spacing={2}>
            <Grid item sm={6} xs={12}>
              <ButtonGroup
                color="container"
                aria-label="outlined primary button group"
              >
                <Button
                  startIcon={<DescriptionIcon />}
                  // onClick={() => handleOpenForm()}
                  type="submit"
                >
                  {!isMobile && "Lưu lại"}
                </Button>
                <Button
                  // disabled={listSelected?.length === 0}
                  startIcon={<ClearIcon />}
                  onClick={() => {
                    history.push(
                      ConstantList.ROOT_PATH + "category/staff/reward"
                    );
                  }}
                >
                  {!isMobile && "Hủy lưu"}
                </Button>
              </ButtonGroup>
            </Grid>

            <Grid item xs={12}>
              <div className="index-card p-16">
                <Grid container spacing={2}>
                  <Grid item sm={3} xs={12}>
                    <GlobitsTextField name="code" label={t("reward.code")} />
                  </Grid>
                  <Grid item sm={3} xs={12}>
                    <GlobitsTextField name="name" label={t("reward.name")} />
                  </Grid>
                  <Grid item sm={3} xs={12}>
                    <GlobitsTextField
                      name="languageKey"
                      label={t("reward.languageKey")}
                    />
                  </Grid>
                  <Grid item sm={3} xs={12}>
                    <GlobitsTextField
                      name="rewardType"
                      label={t("reward.rewardType")}
                    />
                  </Grid>
                  <Grid item md={3} xs={12}>
                    <GlobitsTextField
                      type="number"
                      validate
                      label={t("reward.formal")}
                      name="formal"
                    />
                  </Grid>

                  <Grid item md={3} xs={12}>
                    <GlobitsTextField
                      type="number"
                      validate
                      label={t("reward.evaluateYear")}
                      name="evaluateYear"
                    />
                  </Grid>

                  <Grid item md={3} xs={12}>
                    <GlobitsTextField
                      type="number"
                      validate
                      label={t("reward.evaluateLevel")}
                      name="evaluateLevel"
                    />
                  </Grid>

                  <Grid item xs={12}>
                    <GlobitsTextField
                      label={t("reward.description")}
                      name="description"
                      multiline
                      rows={3}
                    />
                  </Grid>
                </Grid>
              </div>
            </Grid>
          </Grid>
        </div>
      </Form>
    </Formik>
  );
}

export default memo(observer(RewardForm));
