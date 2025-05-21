import { Icon, Card, Grid, Divider, Button, DialogActions, Dialog } from "@material-ui/core";
import React, { memo } from "react";
import DialogContent from '@material-ui/core/DialogContent';
import DialogTitle from '@material-ui/core/DialogTitle';
import { toast } from 'react-toastify';
import Draggable from 'react-draggable';
import Paper from '@material-ui/core/Paper';
import { EgretProgressBar } from "egret";
import axios from "axios";
import ConstantList from "../../appConfig";
import { observer } from "mobx-react";
import GlobitsPopup from "app/common/GlobitsPopup";

function PaperComponent(props) {
    return (
        <Draggable handle="#draggable-dialog-title" cancel={'[class*="MuiDialogContent-root"]'}>
            <Paper {...props} />
        </Draggable>
    );
}

class ImportPSV2ExcelDialog extends React.Component {
    state = {
        dragClass: "",
        files: [],
        statusList: [],
        queProgress: 0
    };

    handleFileUploadOnSelect = event => {
        let files = event.target.files;
        this.fileUpload(files[0]).then(res => {
            alert("File uploaded successfully.")
        });
    }

    handleFileSelect = event => {
        let files = event.target.files;
        let list = [];

        console.log(files);
        let index = 0;
        for (const iterator of files) {
            index++;
            console.log(index);
            list.push({
                file: iterator,
                uploading: false,
                error: false,
                progress: 0
            });
        }

        this.setState({
            files: [...list]
        }, function () {
            console.log(this.state);
            document.getElementById('upload-single-file').value = null;
        });
    };

    handleDragOver = event => {
        event.preventDefault();
        this.setState({ dragClass: "drag-shadow" });
    };

    handleDrop = event => {
        event.preventDefault();
        event.persist();

        let files = event.dataTransfer.files;
        let list = [];

        for (const iterator of files) {
            list.push({
                file: iterator,
                uploading: false,
                error: false,
                progress: 0
            });
        }

        this.setState({
            dragClass: "",
            files: [...list]
        });

        return false;
    };

    handleDragStart = event => {
        this.setState({ dragClass: "drag-shadow" });
    };

    handleSingleRemove = index => {
        let files = [...this.state.files];
        files.splice(index, 1);
        this.setState({
            files: [...files]
        }, function () {
            console.log(this.state);
        });
    };

    handleAllRemove = () => {
        this.setState({ files: [] });
    };

    fileUpload(file) {
        const url = ConstantList.API_ENPOINT + "/api/hr/file/importPositionTitle";
        let formData = new FormData();
        formData.append('uploadfile', file);//Lưu ý tên 'uploadfile' phải trùng với tham số bên Server side
        const config = {
            headers: {
                'Content-Type': 'multipart/form-data'
            }
        }
        return axios.post(url, formData, config);
    };

    uploadSingleFile = index => {
        let allFiles = [...this.state.files];
        let file = this.state.files[index];
        this.fileUpload(file.file).then(res => {
            toast.success('general.success')
        })

        allFiles[index] = { ...file, uploading: true, error: false };

        this.setState({
            files: [...allFiles]
        });
    };

    uploadAllFile = () => {
        let allFiles = [];

        this.state.files.map(item => {
            allFiles.push({
                ...item,
                uploading: true,
                error: false
            });

            return item;
        });

        this.setState({
            files: [...allFiles],
            queProgress: 35
        });
    };

    handleSingleCancel = index => {
        let allFiles = [...this.state.files];
        let file = this.state.files[index];

        allFiles[index] = { ...file, uploading: false, error: true };

        this.setState({
            files: [...allFiles]
        });
    };

    handleCancelAll = () => {
        let allFiles = [];

        this.state.files.map(item => {
            allFiles.push({
                ...item,
                uploading: false,
                error: true
            });

            return item;
        });

        this.setState({
            files: [...allFiles],
            queProgress: 0
        });
    };

    render() {
        const { t, handleClose, open } = this.props;
        let { files } = this.state;
        let isEmpty = files.length === 0;
        return (
            <GlobitsPopup
                scroll={"body"}
                size="md"
                open={open}
                noDialogContent
                title={t("general.button.importExcel")}
                onClosePopup={handleClose}
            >
                <DialogContent>
                    <div className="upload-form m-sm-30">
                        <div className="flex flex-wrap mb-24">
                            <label htmlFor="upload-single-file">
                                <Button
                                    size="small"
                                    className="capitalize"
                                    component="span"
                                    variant="contained"
                                    color="primary"
                                >
                                    <div className="flex flex-middle">
                                        <Icon className="pr-8">cloud_upload</Icon>
                                        <span>{t('general.button.selectFile')}</span>
                                    </div>
                                </Button>
                            </label>
                            <input
                                className="display-none"
                                onChange={this.handleFileSelect}
                                id="upload-single-file"
                                type="file"
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
                                        {t('general.fileName')}
                                    </Grid>
                                    <Grid item lg={4} md={4}>
                                        {t('general.size')}
                                    </Grid>
                                    <Grid item lg={4} md={4}>
                                        {t('general.action')}
                                    </Grid>
                                </Grid>
                            </div>
                            <Divider></Divider>

                            {isEmpty && <p className="px-16 center">{t('general.emptyFile')}</p>}

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
                                            <Grid item lg={4} md={4} sm={12} xs={12}>
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
                                            </Grid>
                                            <Grid item lg={4} md={4} sm={12} xs={12}>
                                                <div className="flex">
                                                    <Button
                                                        variant="contained"
                                                        color="primary"
                                                        disabled={uploading}
                                                        onClick={() => this.uploadSingleFile(index)}
                                                    >
                                                        {t('general.button.upload')}
                                                    </Button>
                                                    <Button
                                                        className="mx-8"
                                                        variant="contained"
                                                        disabled={!uploading}
                                                        color="secondary"
                                                        onClick={() => this.handleSingleCancel(index)}
                                                    >
                                                        {t('general.button.cancel')}
                                                    </Button>
                                                    <Button
                                                        variant="contained"
                                                        className="bg-error"
                                                        onClick={() => this.handleSingleRemove(index)}
                                                    >
                                                        {t('general.button.remove')}
                                                    </Button>
                                                </div>
                                            </Grid>
                                        </Grid>
                                    </div>
                                );
                            })}
                        </Card>
                    </div>
                </DialogContent>

                <div className="dialog-footer py-8">
                    <DialogActions className="p-0">
                        <div className="flex flex-space-between flex-middle">
                            <Button
                                startIcon={<BlockIcon />}
                                variant="contained"
                                className="mr-12 btn btn-secondary d-inline-flex"
                                color="secondary"
                                onClick={handleClose}
                                disabled={isSubmitting}
                            >
                                {t("general.button.cancel")}
                            </Button>
                            {/* <Button
                                startIcon={<SaveIcon />}
                                className="mr-0 btn btn-primary d-inline-flex"
                                variant="contained"
                                color="primary"
                                type="submit"
                                disabled={isSubmitting}
                            >
                                {t("general.button.save")}
                            </Button> */}
                        </div>
                    </DialogActions>
                </div>

                {/* <DialogActions>
                    <Button
                        className="mb-16 mr-36 align-bottom"
                        variant="contained"
                        color="secondary"
                        onClick={() => handleClose()}>{t('general.button.close')}
                    </Button>
                </DialogActions> */}
            </GlobitsPopup>
        )
    }
}
export default memo(observer(ImportPSV2ExcelDialog));