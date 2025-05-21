import React from "react";
import Typography from "@material-ui/core/Typography";
import Icon from "@material-ui/core/Icon";
import Avatar from "@material-ui/core/Avatar";
import CloudUploadIcon from "@material-ui/icons/CloudUpload";
import withStyles from "@material-ui/core/styles/withStyles";
import customImageInputStyle from "./CustomImageInputStyle";
import classnames from "classnames";
import { Button } from "@material-ui/core";
import ConstantList from "../../../appConfig";
import GlobitsAvatar from "app/common/GlobitsAvatar";
import { getImageNameAndType } from "app/LocalFunction";

class ImageInput extends React.Component {
  constructor(props) {
    super(props);
    this.fileUpload = React.createRef();
    this.showFileUpload = this.showFileUpload.bind(this);
    this.handleImageChange = this.handleImageChange.bind(this);
  }

  state = {
    file: undefined,
    imagePreviewUrl: undefined,
  };


  showFileUpload() {
    if (this.fileUpload) {
      this.fileUpload.current.click();
    }
  }

  handleImageChange(e) {
    e.preventDefault();
    let reader = new FileReader();
    let file = e.target.files[0];
    if (file) {
      reader.onloadend = () => {
        this.setState({
          file: file,
          imagePreviewUrl: reader.result,
        });
      };
      reader.readAsDataURL(file);
      this.props.onChange(this.props.field.name, file);
    }
  }

  handleRemoveImage() {
    document.getElementsByClassName("img-input")[0].value = null;
  }

  showPreloadImage() {
    const { errorMessage, classes, imagePath } = this.props;
    const { file, imagePreviewUrl } = this.state;

    let comp = null;

    if (errorMessage) {
      comp = <Icon style={{ fontSize: 200 }}>error_outline</Icon>;
    } else if (file) {
      comp = (
        <img className={classes.avatarThumb} src={imagePreviewUrl} alt="..." />
      );
    } else if (imagePath) {
      comp = (
        <img
          className={classes.avatarThumb}
          alt="avatar"
          src={
            ConstantList.API_ENPOINT +
            "/public/hr/file/getImage/" +
            getImageNameAndType(imagePath)
          }
        />
      );
    } else {
      comp = <Icon style={{ fontSize: 320 }}>account_circle</Icon>;
    }
    return comp;
  }

  render() {
    const { errorMessage, title, classes, imagePath, nameStaff } = this.props;
    const { name } = this.props.field;

    const avatarStyle = classnames(
      classes.bigAvatar,
      this.state.file ? [classes.whiteBack] : [classes.primaryBack],
      { [classes.errorBack]: errorMessage }
    );

    console.log()

    return (
      <div className={`${classes.container} mb-0`}>
        <input
          className={`${classes.hidden} img-input`}
          id={name}
          name={name}
          type="file"
          onChange={this.handleImageChange}
          ref={this.fileUpload}
        />

        {title && title?.length() > 0 && (
          <Typography className={classes.title} variant="h5">
            {title}
          </Typography>
        )}

        {
          this.props.disabled && (
            <img src={ConstantList.API_ENPOINT +
              "/public/hr/file/getImage/" +
              getImageNameAndType(imagePath)}
              style={{
                ...this.props?.wrapperAvatarStyle
              }}
            />
            // <Avatar
            //   color="inherit"
            //   className={avatarStyle}
            //   onClick={this.showFileUpload}
            //   style={{
            //     ...this.props?.wrapperAvatarStyle
            //   }}
            // >
            //   <GlobitsAvatar
            //     imgPath={this.state.file ? this.state.imagePreviewUrl : imagePath}
            //     style={{ fontSize: 70 }}
            //     name={nameStaff} isFile={this.state.file ? true : false}
            //   />
            // </Avatar>
          )
        }

        {
          !this.props.disabled && (
            <Avatar
              color="inherit"
              className={`${avatarStyle} mb-8`}
              onClick={this.showFileUpload}
              style={{
                ...this.props?.wrapperAvatarStyle
              }}
            >
              <GlobitsAvatar
                imgPath={this.state.file ? this.state.imagePreviewUrl : imagePath}
                style={{ fontSize: 70, objectFit: 'cover', objectPosition: "center" }}
                name={nameStaff} isFile={this.state.file ? true : false}
              />
            </Avatar>
          )
        }

        {!this.props.disabled && ( 
          <div className="flex align-center justify-center">
            <Button
              // startIcon={}
              className="btn bgc-lighter-dark-blue d-inline-flex text-white"
              variant="contained"
              onClick={this.showFileUpload}
            >
              <CloudUploadIcon className="mr-6" />
              Chọn ảnh
            </Button>

            {/* <Button
              startIcon={<CloudUploadIcon />}
              className="mr-0 btn btn-primary d-inline-flex mr-12"
              variant="contained"
              onClick={this.showFileUpload}
            >
              Lưu
            </Button>

            <Button
              startIcon={<BlockIcon />}
              className="mr-0 btn btn-secondary d-inline-flex"
              variant="contained"
              onClick={this.handleRemoveImage}
            >
              Hủy
            </Button> */}
          </div>
        )}

        {errorMessage ? (
          <Typography variant="caption" color="error">
            {errorMessage}
          </Typography>
        ) : null}
      </div>
    );
  }
}

export default withStyles(customImageInputStyle)(ImageInput);
