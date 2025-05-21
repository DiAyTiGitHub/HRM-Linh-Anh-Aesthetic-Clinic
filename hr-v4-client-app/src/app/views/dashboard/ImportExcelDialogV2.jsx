import React from "react";
import {
  Icon,
  Card,
  Grid,
  Divider,
  Button,
  DialogActions,
  Dialog,
  IconButton,
} from "@material-ui/core";
import DialogContent from "@material-ui/core/DialogContent";
import DialogTitle from "@material-ui/core/DialogTitle";
import BlockIcon from "@material-ui/icons/Block";
import CloudUploadIcon from "@material-ui/icons/CloudUpload";
import DeleteIcon from "@material-ui/icons/Delete";
import axios from "axios";
import { toast } from "react-toastify";
import { EgretProgressBar } from "egret";
import ConstantList from "../../appConfig";

import PaperComponent from "app/common/DraggablePopup/PaperComponent";

class ImportExcelDialogV2 extends React.Component {
  state = {
    dragClass: "",
    files: [],
    statusList: [],
    queProgress: 0,
  };
  handleFileSelect = (event) => {
    let files = event.target.files;
    let list = [];
    for (const iterator of files) {
      if (!this.checkfile(iterator)) {
        return;
      }
      list.push({
        file: iterator,
        uploading: false,
        error: false,
        progress: 0,
      });
    }

    this.setState({
      files: [...list],
    });
  };

  handleSingleRemove = (index) => {
    let files = [...this.state.files];
    files.splice(index, 1);
    this.setState({
      files: [...files],
    });
  };

  fileUpload(file) {
    const url = ConstantList.API_ENPOINT + "/api/timesheet/import-timekeeping";
    let formData = new FormData();
    formData.append("uploadfile", file);
    const config = {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    };
    return axios.post(url, formData, config);
  }

  uploadSingleFile = (index) => {
    let allFiles = [...this.state.files];
    let file = this.state.files[index];
    this.fileUpload(file.file)
      .then(() => {
        toast("Đã tải lên thành công.");
        this.props.handleClose();
      })
      .catch((error) => {
        console.error(error);
      });

    allFiles[index] = { ...file, uploading: true, error: false };

    this.setState({
      files: [...allFiles],
    });
  };

  handleSingleCancel = (index) => {
    let allFiles = [...this.state.files];
    let file = this.state.files[index];

    allFiles[index] = { ...file, uploading: false, error: true };

    this.setState({
      files: [...allFiles],
    });
  };

  checkfile = (file) => {
    var validExts = [".xlsx", ".xls"];
    file = file?.name.substring(file?.name.lastIndexOf("."));
    if (validExts.indexOf(file) < 0) {
      toast.error("File được chọn không đúng định dạng excel!");
      return false;
    } else return true;
  };

  render() {
    const { t, handleClose, open } = this.props;
    let { files } = this.state;
    let isEmpty = files.length === 0;
    console.log(open);
    console.log("props:", this.props);

    return (
      <Dialog
        className="dialog-container"
        open={open}
        PaperComponent={PaperComponent}
        fullWidth
        maxWidth="md"
      >
        <DialogTitle
          className="dialog-header bgc-primary-d1"
          style={{ cursor: "move" }}
          id="draggable-dialog-title"
        >
          <span className="mb-20 text-white">
            {t("general.button.importExcel")}
          </span>
        </DialogTitle>
        <IconButton
          style={{
            position: "absolute",
            right: "10px",
            top: "10px",
          }}
          onClick={() => handleClose()}
        >
          <Icon color="disabled" title={t("general.button.close")}>
            close
          </Icon>
        </IconButton>
        <div className="dialog-body">
          <DialogContent className="o-hidden">
            <div className="upload-form m-sm-30">
              <div className="flex flex-wrap mb-24">
                <label htmlFor="upload-single-file">
                  <Button
                    size="small"
                    className="btn btn-secondary d-inline-flex"
                    startIcon={<CloudUploadIcon />}
                    component="span"
                    variant="contained"
                  >
                    {t("general.button.selectFile")}
                  </Button>
                </label>
                <input
                  className="display-none"
                  onChange={this.handleFileSelect}
                  id="upload-single-file"
                  type="file"
                  accept="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet, application/vnd.ms-excel"
                />
                <div className="px-16"></div>
              </div>
              <Card className="mb-24" elevation={2}>
                <div className="p-16">
                  <Grid
                    container
                    spacing={2}
                    justify="center"
                    alignItems="center"
                    direction="row"
                  >
                    <Grid item lg={4} md={4}>
                      {t("general.button.fileName")}
                    </Grid>
                    <Grid item lg={4} md={4}>
                      {t("general.button.size")}
                    </Grid>
                    <Grid item lg={4} md={4}>
                      {t("general.action")}
                    </Grid>
                  </Grid>
                </div>
                <Divider></Divider>

                {isEmpty && (
                  <p className="px-16 center">{t("general.emptyFile")}</p>
                )}

                {files.map((item, index) => {
                  let { file, uploading, error, progress } = item;
                  return (
                    <div className="px-16 py-16" key={file.name}>
                      <Grid
                        container
                        spacing={2}
                        justify="center"
                        alignItems="center"
                        direction="row"
                      >
                        <Grid
                          item
                          lg={4}
                          md={4}
                          sm={12}
                          xs={12}
                          style={{
                            textOverflow: "ellipsis",
                            whiteSpace: "nowrap",
                            overflow: "hidden",
                          }}
                        >
                          {file.name}
                        </Grid>
                        <Grid item lg={1} md={1} sm={12} xs={12}>
                          {(file.size / 1024 / 1024).toFixed(1)} MB
                        </Grid>
                        <Grid item lg={2} md={2} sm={12} xs={12}>
                          <EgretProgressBar value={progress}></EgretProgressBar>
                        </Grid>
                        <Grid item lg={1} md={1} sm={12} xs={12}>
                          {error && <Icon color="error">error</Icon>}
                          {uploading && (
                            <Icon className="text-green">done</Icon>
                          )}
                        </Grid>
                        <Grid item lg={4} md={4} sm={12} xs={12}>
                          <div className="flex">
                            <Button
                              variant="contained"
                              className="btn btn-success d-inline-flex"
                              startIcon={<CloudUploadIcon />}
                              disabled={uploading}
                              onClick={() => this.uploadSingleFile(index)}
                              component="span"
                            >
                              {t("general.button.upload")}
                            </Button>

                            <Button
                              variant="contained"
                              className="mx-8 btn btn-warning d-inline-flex"
                              startIcon={<DeleteIcon />}
                              onClick={() => this.handleSingleRemove(index)}
                            >
                              {t("general.button.remove")}
                            </Button>

                            {uploading && (
                              <Button
                                className="btn btn-secondary d-inline-flex"
                                variant="contained"
                                startIcon={<BlockIcon />}
                                onClick={() => this.handleSingleCancel(index)}
                              >
                                {t("general.button.cancel")}
                              </Button>
                            )}
                          </div>
                        </Grid>
                      </Grid>
                    </div>
                  );
                })}
              </Card>
            </div>
          </DialogContent>
        </div>
        <div className="dialog-footer">
          <DialogActions className="p-0">
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
            </div>
          </DialogActions>
        </div>
      </Dialog>
    );
  }
}
export default ImportExcelDialogV2;
