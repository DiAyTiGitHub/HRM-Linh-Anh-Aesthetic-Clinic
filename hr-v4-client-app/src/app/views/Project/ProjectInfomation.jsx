import React, { useState, useEffect, memo } from "react";
import { Formik, Form } from "formik";
import { Grid, DialogActions, Button, DialogContent } from "@material-ui/core";
import SaveIcon from "@material-ui/icons/Save";
import BlockIcon from "@material-ui/icons/Block";
import { useTranslation } from "react-i18next";
import { useStore } from "../../stores";
import { observer } from "mobx-react";
import { useParams } from "react-router-dom";
import { pagingStaff } from "../HumanResourcesInformation/StaffService";
import EditIcon from "@material-ui/icons/Edit";
import { useHistory } from "react-router-dom";
import ConstantList from "../../appConfig";
import GlobitsDateTimePicker from "app/common/form/GlobitsDateTimePicker";
import GlobitsCheckBox from "app/common/form/GlobitsCheckBox";
import moment from "moment";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import GlobitsPagingAutocomplete from "app/common/form/GlobitsPagingAutocomplete";
import GlobitsEditor from "app/common/form/GlobitsEditor";

function ProjectInfomation() {
  const { t } = useTranslation();
  const { id } = useParams();
  const [isEdit, setIsEdit] = useState(false);
  const history = useHistory();

  const { projectStore } = useStore(); 
  const {
    dataEditProject,
    handleSubmitFormProject,
    // getListActivityById,
    canCreateUpdateDeleteProject,
  } = projectStore;

  const { handlePagingStaff } = projectStore;

  const [options, setOptions] = useState([]);

  useEffect(() => {
    if (id !== "create") {
      //do not call api to load project's activity here
      // getListActivityById(id);
    } else {
      setIsEdit(true);
    }

    if (id == "create") handlePagingStaff().then((data) => setOptions(data));
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  return (
    <div className="dialog-body">
      {isEdit ? (
        <Formik
          enableReinitialize
          initialValues={dataEditProject}
          onSubmit={(values) => {
            handleSubmitFormProject(values, id)
              .then(() => {
                setIsEdit(false);
              });
          }}
        >
          {({ isSubmitting, resetForm }) => (
            <Form autoComplete="off">
              <DialogActions className="p-0">
                <div className="flex flex-space-between flex-middle">
                  <Button
                    startIcon={<BlockIcon />}
                    variant="contained"
                    className="mr-12 btn btn-secondary d-inline-flex"
                    color="secondary"
                    onClick={() => {
                      if (dataEditProject?.id) {
                        setIsEdit(false);
                        resetForm();
                      } else {
                        history.push(
                          ConstantList.ROOT_PATH + `timesheet/project`
                        );
                      }
                    }}
                  >
                    {t("general.button.cancel")}
                  </Button>
                  <Button
                    startIcon={<SaveIcon />}
                    className="mr-0 btn btn-primary d-inline-flex"
                    variant="contained"
                    color="primary"
                    type="submit"
                  >
                    {t("general.button.save")}
                  </Button>
                </div>
              </DialogActions>
              <div className="dialog-body">
                <DialogContent className="o-hidden">
                  <Grid container spacing={2}>
                    <Grid item md={6} sm={12} xs={12}>
                      <GlobitsTextField
                        label={t("project.code")}
                        name="code"
                        validate={true}
                      />
                    </Grid>
                    <Grid item md={6} sm={12} xs={12}>
                      <GlobitsTextField label={t("project.name")} name="name" />
                    </Grid>
                    <Grid item xs={12}>
                      <GlobitsPagingAutocomplete
                        name="projectStaff"
                        label={"Nhân viên thực hiện"}
                        options={options}
                        displayData="displayName"
                        multiple
                        api={pagingStaff}
                        displayName="displayName"
                        isMulti
                      />
                    </Grid>
                    <Grid item xs={12} md={6}>
                      <GlobitsDateTimePicker
                        label={"Ngày bắt đầu"}
                        name="startDate"
                      />
                    </Grid>
                    <Grid item xs={12} md={6}>
                      <GlobitsDateTimePicker
                        label={"Ngày kết thúc"}
                        name="endDate"
                      />
                    </Grid>
                    <Grid item md={12} sm={12} xs={12}>
                      {/* <GlobitsTextField
                        label={t("project.description")}
                        name="description"
                        rows={3}
                        multiline
                      /> */}
                      {t("project.description")}
                      <GlobitsEditor
                        name="description"
                        placeholder="Mô tả công việc..."
                      />
                    </Grid>
                    <Grid item xs={12} sm={4}>
                      <GlobitsCheckBox label={"Đã Kết thúc"} name="finished" />
                    </Grid>
                  </Grid>
                </DialogContent>
              </div>
            </Form>
          )}
        </Formik>
      ) : (
        <DialogContent className="o-hidden">
          <div
            className="d-inline-flex"
            style={{ justifyContent: "flex-end", width: "100%" }}
          >
            {canCreateUpdateDeleteProject() && (
              <Button
                className="mb-16 btn btn-primary d-inline-flex"
                variant="contained"
                onClick={() => setIsEdit(true)}
                style={{ color: "white" }}
              >
                <EditIcon /> Cập nhật thông tin
              </Button>
            )}
          </div>
          <Grid container spacing={2}>
            <Grid item md={6} sm={12} xs={12}>
              <strong>Mã dự án:</strong>
              <p
                style={{
                  fontWeght: "500",
                  fontSize: "25px",
                  marginTop: "unset",
                }}
              >
                {dataEditProject?.code || ""}
              </p>
            </Grid>
            <Grid item md={6} sm={12} xs={12}>
              <strong>Tên dự án:</strong>
              <p
                style={{
                  fontWeght: "500",
                  fontSize: "25px",
                  marginTop: "unset",
                }}
              >
                {dataEditProject?.name || ""}
              </p>
            </Grid>
            <Grid item xs={12}>
              <strong> Các nhân viên thực hiện:</strong>
              <div>
                {dataEditProject?.projectStaff?.length > 0 &&
                  dataEditProject.projectStaff.map((item, index) => (
                    <p
                      key={index}
                      className="tag bgc-primary"
                      style={{
                        display: "inline-block",
                        borderRadius: "3px",
                        padding: "5px",
                        color: "#fff",
                        margin: 3,
                      }}
                    >
                      {item?.displayName}
                    </p>
                  ))}
              </div>
            </Grid>
            {
              dataEditProject?.startDate && (
                <Grid item md={6} sm={6} xs={6}>
                  <strong>Ngày bắt đầu:</strong>
                  <p style={{ fontWeght: "bold" }}>
                    {dataEditProject?.startDate
                      ? moment(dataEditProject?.startDate).format("DD/MM/YYYY")
                      : ""}
                  </p>
                </Grid>
              )
            }

            {dataEditProject?.endDate && (
              <Grid item md={6} sm={6} xs={6}>
                <strong>Ngày kết thúc:</strong>
                <p style={{ fontWeght: "bold" }}>
                  {dataEditProject?.endDate
                    ? moment(dataEditProject?.endDate).format("DD/MM/YYYY")
                    : ""}
                </p>
              </Grid>
            )}

            {
              dataEditProject?.description && (
                <Grid item md={12} sm={12} xs={12}>
                  <strong>Mô tả:</strong>
                  {/* <p style={{ fontWeght: "bold" }}>
      {dataEditProject?.description || ""}
    </p> */}

                  <div dangerouslySetInnerHTML={{ __html: dataEditProject?.description }}></div>
                </Grid>
              )
            }


            {dataEditProject?.finished && (
              <Grid item md={12} sm={12} xs={12}>
                <strong>Đã kết thúc</strong>
              </Grid>
            )}
          </Grid>
        </DialogContent>
      )}
    </div>
  );
}

export default memo(observer(ProjectInfomation));
