import React, { memo } from "react";
import {
  Icon,
  Card,
  Grid,
  Divider,
  Button,
  DialogActions,
  Dialog,
  IconButton,
  Tooltip,
  ButtonGroup,
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
import GetAppIcon from '@material-ui/icons/GetApp';
import PaperComponent from "app/common/DraggablePopup/PaperComponent";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import GlobitsTable from "app/common/GlobitsTable";
import { downloadImportV1Template } from "../TimeSheet/TimeSheetService";
import { saveAs } from "file-saver";

class ImportTimesheetDataPopup extends React.Component {
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
    const url = ConstantList.API_ENPOINT + "/api/timesheet/handle-upload-csv";
    let formData = new FormData();
    formData.append("upload", file);
    const config = {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    };
    return axios.post(url, formData, config);
  }

  uploadSingleFile = async (index) => {
    try {
      let allFiles = [...this.state.files];
      let file = this.state.files[index];

      const response = await this.fileUpload(file.file);

      toast.success("Dữ liệu chấm công đã được cập nhật");

      await this.props.renewDataFunction();
      this.props.handleClose();


      allFiles[index] = { ...file, uploading: true, error: false };

      this.setState({
        files: [...allFiles],
      });
    }
    catch (error) {
      console.error(error);
      toast.error("Có lỗi xảy ra trong quá trình tải dữ liệu");
    }

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

  handleDownloadImportV1Template = async () => {
    try {
      const res = await downloadImportV1Template();
      let blob = new Blob([res.data], {
        type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
      });

      saveAs(blob, "Mẫu nhập dữ liệu chấm công - Mẫu 1.xlsx");
    } finally {
    }
  }

  render() {
    const {
      t,
      handleClose,
      open,
      renewDataFunction
    } = this.props;
    let { files } = this.state;
    let isEmpty = files.length === 0;

    const columns = [
      {
        title: t("general.action"),
        width: "10%",
        align: "center",
        render: (item) => {
          const index = files.indexOf(item); // Calculate the index of the current row
          const { file, uploading, error, progress } = item;

          return (
            <div className="flex flex-middle justify-center">
              <Tooltip title={t("general.button.upload")} placement="top">
                <IconButton size="small"
                  onClick={() => this.uploadSingleFile(index)}
                >
                  <Icon fontSize="small" color="primary">
                    cloud_upload
                  </Icon>
                  {/* <CloudUploadIcon /> */}
                </IconButton>
              </Tooltip>

              <Tooltip title={t("general.button.remove")} placement="top">
                <IconButton size="small"
                  onClick={() => this.handleSingleRemove(index)}
                >
                  <Icon fontSize="small" color="secondary">
                    delete
                  </Icon>
                </IconButton>
              </Tooltip>

              {uploading && (
                <Tooltip
                  title={t("general.button.cancel")}
                  placement="top"
                >
                  <IconButton size="small"
                    onClick={() => this.handleSingleCancel(index)}
                  >
                    <Icon fontSize="small" style={{ color: "orange" }}>
                      block
                    </Icon>
                  </IconButton>
                </Tooltip>
              )}

            </div >
          );
        },
      },
      {
        title: t("general.button.fileName"),
        align: "center",
        field: "fileName",
        render: item => {
          const { file, uploading, error, progress } = item;

          return (
            <span
              style={{
                textOverflow: "ellipsis",
                whiteSpace: "nowrap",
                overflow: "hidden",
              }}
            >
              {file.name}
            </span>
          );
        }
      },
      {
        title: t("general.button.size"),
        align: "center",
        field: "displayName",
        render: item => {
          const { file, uploading, error, progress } = item;

          return (
            <Grid container spacing={2}>
              <Grid item xs={4}>
                <div className="h-100 flex align-center justify-center">
                  {(file.size / 1024 / 1024).toFixed(1)} MB
                </div>
              </Grid>
              <Grid item xs={4}>
                <div className="h-100 flex align-center justify-center">
                  <EgretProgressBar value={progress}></EgretProgressBar>
                </div>
              </Grid>
              <Grid item xs={4}>
                {error && <Icon color="error">error</Icon>}
                {uploading && (
                  <Icon className="text-green">done</Icon>
                )}
              </Grid>
            </Grid>
          );
        }
      },
    ];

    return (
      <GlobitsPopupV2
        scroll={"body"}
        size="md"
        open={open}
        noDialogContent
        title={"Nhập dữ liệu chấm công theo mẫu 1"}
        onClosePopup={handleClose}
      >
        <div className="dialog-body">
          <DialogContent className="p-12">
            <Grid container spacing={2}>
              <Grid item xs={12}>
                <ButtonGroup color="container" aria-label="outlined primary button group">
                  <Button
                    startIcon={<GetAppIcon />}
                    onClick={this.handleDownloadImportV1Template}
                  >
                    Tải mẫu nhập
                  </Button>

                  <Button
                    startIcon={<CloudUploadIcon />}
                    component="span"
                  >
                    {t("general.button.selectFile")} dữ liệu
                    <input
                      type="file"
                      onChange={this.handleFileSelect}
                      accept="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet, application/vnd.ms-excel"
                      style={{
                        position: "absolute",
                        opacity: 0,
                        width: "100%",
                        height: "100%",
                        cursor: "pointer",
                      }}
                    />
                  </Button>

                </ButtonGroup>
              </Grid>

              <Grid item xs={12}>
                <GlobitsTable
                  data={files}
                  nonePagination
                  columns={columns}
                />
              </Grid>
            </Grid>
          </DialogContent>
        </div>
      </GlobitsPopupV2>

    );
  }
}
export default memo(ImportTimesheetDataPopup);
