import React, { useState, useEffect } from "react";
import { Formik, Form } from "formik";
import { Grid, DialogActions, Button, DialogContent } from "@material-ui/core";
import SaveIcon from "@material-ui/icons/Save";
import BlockIcon from "@material-ui/icons/Block";
import { useTranslation } from "react-i18next";
import { useStore } from "../../stores";
import { observer } from "mobx-react";
import * as Yup from "yup";

import GlobitsTextField from "../../common/form/GlobitsTextField";

export default observer(function ProjectForm() {
  const { timeSheetStore } = useStore();
  const { t } = useTranslation();
  const { handleClose, createProject, editProject, selectedProject } =
    timeSheetStore;

  const [project, setProject] = useState({
    id: "",
    code: "",
    name: "",
    description: "",
  });

  const validationSchema = Yup.object({
    code: Yup.string().required(t("validation.required")),
    name: Yup.string().required(t("validation.required")),
  });

  useEffect(() => {
    if (selectedProject) setProject(selectedProject);
  }, [selectedProject]);

  return (
    <Formik
      validationSchema={validationSchema}
      enableReinitialize
      initialValues={project}
      onSubmit={(values) =>
        values.id.length === 0 ? createProject(values) : editProject(values)
      }
    >
      {({ isSubmitting }) => (
        <Form autoComplete="off">
          <div className="dialog-body">
            <DialogContent className="o-hidden">
              <Grid container spacing={2}>
                <Grid item md={12} sm={12} xs={12}>
                  <GlobitsTextField
                    label={
                      <span>
                        <span style={{ color: "red" }}> * </span>
                        {t("project.code")}
                      </span>
                    }
                    name="code"
                  />
                </Grid>
                <Grid item md={12} sm={12} xs={12}>
                  <GlobitsTextField
                    label={
                      <span>
                        <span style={{ color: "red" }}> * </span>
                        {t("project.name")}
                      </span>
                    }
                    name="name"
                  />
                </Grid>
                <Grid item md={12} sm={12} xs={12}>
                  <GlobitsTextField
                    label={t("project.description")}
                    name="description"
                    multiline
                    rows={3}
                  />
                </Grid>
              </Grid>
            </DialogContent>
          </div>
          <div className="dialog-footer">
            <DialogActions className="p-0 ">
              <div className="flex flex-space-between flex-middle">
                <Button
                  startIcon={<BlockIcon />}
                  variant="contained"
                  className="mr-12 btn btn-secondary d-inline-flex"
                  color="secondary"
                  onClick={() => handleClose()}
                >
                  {t("general.button.cancel")}
                </Button>
                <Button
                  startIcon={<SaveIcon />}
                  className="mr-0 btn btn-primary d-inline-flex"
                  variant="contained"
                  color="primary"
                  type="submit"
                  disabled={isSubmitting}
                >
                  {t("general.button.save")}
                </Button>
              </div>
            </DialogActions>
          </div>
        </Form>
      )}
    </Formik>
  );
});
