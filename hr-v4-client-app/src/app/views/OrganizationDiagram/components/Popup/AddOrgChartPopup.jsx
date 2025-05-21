import React from "react";
import {Button, DialogActions, DialogContent, Grid} from "@material-ui/core";
import SaveIcon from "@material-ui/icons/Save";
import BlockIcon from "@material-ui/icons/Block";
import GlobitsPopup from "app/common/GlobitsPopup";
import { Form, Formik } from "formik";
import FormikFocusError from "app/common/FormikFocusError";
import * as Yup from "yup";
import GlobitsTextField from "app/common/form/GlobitsTextField";

export default function AddOrgChartPopup({
  open,
  handleClose,
  handleFormSubmit
}) {
  const validationSchema = Yup.object({

  });

  return (
    <GlobitsPopup
      popupId="edit-node-popup"
      scroll={"body"}
      size="sm"
      open={open}
      noDialogContent
      title={"Thêm mới sơ đồ"}
      onClosePopup={handleClose}
    >
      <Formik
        validationSchema={validationSchema}
        enableReinitialize
        initialValues={{name: "", code: ""}}
        onSubmit={(values) => handleFormSubmit(values)}
      >
        {({ isSubmitting, values, setFieldValue, initialValues }) => {

          return (
            <Form autoComplete="off" autocomplete="off">
              <div className="dialog-body">
                <DialogContent className="p-12">
                  <FormikFocusError />
                  <Grid container spacing={2}>
                    <Grid item xs={12}>
                      <GlobitsTextField label="Tên sơ đồ" name="name" />
                    </Grid>
                    <Grid item xs={12}>
                      <GlobitsTextField label="Mã sơ đồ" name="code" />
                    </Grid>
                  </Grid>
                </DialogContent>
              </div>

              <div className="dialog-footer py-8">
                <DialogActions className="p-0">
                  <div className="flex flex-space-between flex-middle">
                    <Button
                      startIcon={<BlockIcon />}
                      className="btn btn-secondary d-inline-flex mr-8"
                      onClick={() => handleClose()}
                    >
                      Huỷ
                    </Button>
                    <Button
                      startIcon={<SaveIcon />}
                      className="btn btn-primary d-inline-flex"
                      type="submit"
                      disabled={isSubmitting}
                    >
                      Lưu
                    </Button>
                  </div>
                </DialogActions>
              </div>
            </Form>
          );
        }
        }
      </Formik>
    </GlobitsPopup>
  )
}
