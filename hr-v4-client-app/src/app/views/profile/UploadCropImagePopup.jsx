import { Icon, Button } from "@material-ui/core";
import React from "react";
import axios from "axios";
import ConstantList from "../../appConfig";
import ReactCrop from 'react-image-crop';
import 'react-image-crop/dist/ReactCrop.css';
import GlobitsPopup from "app/common/GlobitsPopup";

class UploadCropImagePopup extends React.Component {
  state = {
    dragClass: "",
    files: [],
    statusList: [],
    isEmpty: true,
    src: null,
    crop: {
      unit: '%',
      width: 50,
      height: 50,
    },
    blobValue: null,
    contentType: null
  }
  onImageLoaded = image => {
    this.imageRef = image;
  };

  onCropComplete = crop => {
    this.makeClientCrop(crop);
  };

  onCropChange = (crop, percentCrop) => {
    this.setState({ crop });
  };

  async makeClientCrop(crop) {
    if (this.imageRef && crop.width && crop.height) {
      const croppedImageUrl = await this.getCroppedImg(
        this.imageRef,
        crop,
        'newFile.jpeg'
      );
      this.setState({ croppedImageUrl });
    }
  }

  getCroppedImg(image, crop, fileName) {
    const canvas = document.createElement('canvas');
    const scaleX = image.naturalWidth / image.width;
    const scaleY = image.naturalHeight / image.height;
    canvas.width = crop.width;
    canvas.height = crop.height;
    const ctx = canvas.getContext('2d');

    ctx.drawImage(
      image,
      crop.x * scaleX,
      crop.y * scaleY,
      crop.width * scaleX,
      crop.height * scaleY,
      0,
      0,
      crop.width,
      crop.height
    );

    return new Promise((resolve, reject) => {
      canvas.toBlob(blob => {
        if (!blob) {
          console.error('Canvas is empty');
          return;
        }
        blob.name = fileName;
        window.URL.revokeObjectURL(this.fileUrl);
        this.fileUrl = window.URL.createObjectURL(blob);
        this.setState({ blobValue: blob });
        resolve(this.fileUrl);
      }, 'image/jpeg');
    });
  }
  handleFileUploadOnSelect = e => {
    if (e.target.files && e.target.files.length > 0) {
      const reader = new FileReader();
      reader.addEventListener('load', () =>
        this.setState({ src: reader.result })
      );
      reader.readAsDataURL(e.target.files[0]);
    }
  }
  handleFileSelect = e => {

    if (e.target.files && e.target.files.length > 0) {
      const reader = new FileReader();
      reader.addEventListener('load', () =>
        this.setState({ src: reader.result, isEmpty: false })
      );
      reader.readAsDataURL(e.target.files[0]);
    }
  };

  handleDragOver = event => {
    event.preventDefault();
    this.setState({ dragClass: "drag-shadow" });
  };
  handleDrop = event => {
    event.preventDefault();
    event.persist();
    let files = event.dataTransfer.files;
    if (files && files.length > 0) {
      const reader = new FileReader();
      reader.addEventListener('load', () =>
        this.setState({ src: reader.result })
      );
      reader.readAsDataURL(files[0]);
    }
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
    });
  };

  handleAllRemove = () => {
    this.setState({ files: [] });
  };
  fileUpload(file) {
    const url = this.props.uploadUrl;
    let formData = new FormData();
    formData.append('uploadfile', file);//Lưu ý tên 'uploadfile' phải trùng với tham số bên Server side
    const config = {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    }
    return axios.post(url, formData, config)
  }

  uploadSingleFile = index => {
    let { t } = this.props;
    let allFiles = [...this.state.files];
    let file = this.state.files[index];
    this.fileUpload(file.file).then(res => {
      alert(t("general.success"))
      window.location.reload();
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
  fileUploadBlob = () => {
    const url = ConstantList.API_ENPOINT + "/api/users/updateavatar";
    let formData = new FormData();
    formData.set('uploadfile', this.state.blobValue)
    const config = {
      headers: {
        'Content-Type': 'image/jpg'
      }
    }
    return axios.post(url, formData, config).then(response => {
      let user = response.data;
      this.setState({ user: user });
    });
  }
  render() {
    const { t, handleClose, open, handleUpdate } = this.props;
    let { dragClass, files, } = this.state;

    return (
      <GlobitsPopup
        open={open}
        onClosePopup={handleClose}
        title={t("general.upload")}
        action={<>
          <Button
            className="mb-16 mr-36 align-bottom"
            variant="contained"
            color="secondary"
            onClick={() => handleUpdate(this.state.blobValue)}>{t('general.update')}
          </Button>
          <Button
            className="mb-16 mr-36 align-bottom"
            variant="contained"
            color="secondary"
            onClick={() => handleClose(this.state.user)}>{t('general.close')}
          </Button>
        </>}
      >
        <div className="upload-form m-sm-30">
          {this.state.isEmpty ? (
            <div>
              <div
                className={`${dragClass} upload-drop-box flex flex-center flex-middle`}
                onDragEnter={this.handleDragStart}
                onDragOver={this.handleDragOver}
                onDrop={this.handleDrop}
              >
                {this.state.isEmpty ? (
                  <span>Drop your files here</span>
                ) : (
                  <h5 className="m-0">
                    {files.length} file{files.length > 1 ? "s" : ""} selected...
                  </h5>
                )}
              </div>
              <div className="flex flex-wrap mb-20">
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
                      <span>{t('general.select_file')}</span>
                    </div>
                  </Button>
                </label>
                <input
                  className="display-none"
                  onChange={this.handleFileSelect}
                  id="upload-single-file"
                  type="file"
                  accept="image/*"
                />
              </div>
            </div>
          ) : (
            <div>
              {this.state.src && (
                <ReactCrop style={{ maxWidth: '100%', maxHeight: 400, alignContent: 'center' }}
                  src={this.state.src}
                  crop={this.state.crop}
                  onImageLoaded={this.onImageLoaded}
                  onComplete={this.onCropComplete}
                  onChange={this.onCropChange}
                />
              )}
            </div>
          )}
        </div>
      </GlobitsPopup>
    );
  }
}
export default UploadCropImagePopup;