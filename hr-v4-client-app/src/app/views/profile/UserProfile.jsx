import React, { Component } from "react";
import ConstantList from "../../appConfig";
import {
  Avatar, Grid, Select, FormControl,
  Button,
  withStyles,
  InputLabel,
  TextField,
} from "@material-ui/core";
import { Breadcrumb } from "egret";
import localStorageService from "../../services/localStorageService";
import MenuItem from "@material-ui/core/MenuItem";
import { getCurrentUser } from "./ProfileService";
import UploadCropImagePopup from "./UploadCropImagePopup";
import authService from "../../services/jwtAuthService";
import axios from "axios";
import { Link } from "react-router-dom";

const API_PATH = ConstantList.API_ENPOINT + "/api/fileUpload/";
class UserProfile extends Component {
  state = {
    open: true,
    user: {},
    shouldOpenImageDialog: false,
    shouldOpenPasswordDialog: false,
  };
  windowResizeListener;

  toggleSidenav = () => {
    this.setState({
      open: !this.state.open,
    });
  };

  handleWindowResize = () => {
    return (event) => {
      if (event.target.innerWidth < 768) {
        this.setState({ mobile: true });
      } else this.setState({ mobile: false });
    };
  };

  componentDidMount() {
    getCurrentUser().then(({ data }) => {
      this.setState({ user: data });
      console.log("user", this.state.user);
    });
    if (window.innerWidth < 768) {
      this.setState({ open: false });
    }
    if (window)
      this.windowResizeListener = window.addEventListener("resize", (event) => {
        if (event.target.innerWidth < 768) {
          this.setState({ open: false });
        } else this.setState({ open: true });
      });
  }

  componentWillUnmount() {
    let user = localStorageService.getLoginUser();
    this.setState({ user: user });
    if (window) window.removeEventListener("resize", this.windowResizeListener);
  }
  handleOpenUploadDialog = () => {
    this.setState({
      shouldOpenImageDialog: true,
    });
  };
  handleDialogClose = () => {
    this.setState({
      shouldOpenImageDialog: false,
    });
  };
  handleOpenPasswordDialog = () => {
    this.setState({
      shouldOpenPasswordDialog: true,
    });
  };
  handleDialogPasswordClose = () => {
    this.setState({
      shouldOpenPasswordDialog: false,
    });
  };

  openPasswordDialog = () => {
    this.setState({
      shouldOpenPasswordDialog: true,
    });
  };
  handleUpdate = (blobValue) => {
    const url = ConstantList.API_ENPOINT + "/api/users/updateavatar";
    let formData = new FormData();
    formData.set("uploadfile", blobValue);
    const config = {
      headers: {
        "Content-Type": "image/jpg", 
      },
    };
    return axios.post(url, formData, config).then((response) => {
      let user = response.data;
      this.setState({ user: user });
      authService.setLoginUser(user);
      this.handleDialogClose();
    });
  };

  render() {
    let { t, i18n } = this.props;

    const genders = [
      { id: "M", name: "Nam" },
      { id: "F", name: "Nữ" },
      { id: "U", name: "Không rõ" },
    ];
    let user = this.state.user;
    return (
      <div className="m-sm-30" t={t} i18n={i18n}>
        {this.state.shouldOpenImageDialog && (
          <UploadCropImagePopup
            t={t}
            i18n={i18n}
            handleClose={this.handleDialogClose}
            handleUpdate={this.handleUpdate}
            open={this.state.shouldOpenImageDialog}
            uploadUrl={API_PATH + "avatarUpload"}
            acceptType="png;jpg;gif;jpeg"
          />
        )}

        <div className="mb-sm-30">
          <Breadcrumb
            routeSegments={[
              { name: t("navigation.administration.user") },
              { name: t("navigation.administration.personalInfo") },
            ]}
          />
        </div>
        <div className="user-profile__sidenav flex-column flex-middle">
          {this.state.user && this.state.user ? (
            <Avatar
              className="avatar mb-20"
              src={ConstantList.API_ENPOINT + this.state.user.imagePath}
              onClick={this.handleOpenUploadDialog}
            />
          ) : (
            <div>
              <Avatar
                className="avatar mb-20"
                src={ConstantList.ROOT_PATH + "assets/images/avatar.jpg"}
                onClick={this.handleOpenUploadDialog}
              />
            </div>
          )}
          {user.displayName}
        </div>
        <Grid className="mb-10" container spacing={3}>
          <Grid item md={4} sm={12} xs={12}>
            <FormControl fullWidth={true}>
              <TextField
                id="standard-basic"
                disabled
                label={t("user.first_name")}
                value={user.person != null ? user.person.firstName : ""}
              />
            </FormControl>
          </Grid>

          <Grid item md={4} sm={12} xs={12}>
            <FormControl fullWidth={true}>
              <TextField
                id="standard-basic"
                disabled
                label={t("user.last_name")}
                value={user.person != null ? user.person.lastName : ""}
              />
            </FormControl>
          </Grid>

          <Grid item md={4} sm={12} xs={12}>
            <FormControl fullWidth={true}>
              <TextField
                id="standard-basic"
                disabled
                label={t("user.display_name")}
                value={user.displayName != null ? user.displayName : ""}
              />
            </FormControl>
          </Grid>
        </Grid>

        <Grid className="mb-10" container spacing={3}>
          <Grid item md={4} sm={12} xs={12}>
            <FormControl fullWidth={true}>
              <TextField
                id="standard-basic"
                disabled
                label={t("user.email")}
                value={user.email != null ? user.email : ""}
              />
            </FormControl>
          </Grid>

          <Grid item md={4} sm={12} xs={12}>
            <FormControl fullWidth={true}>
              <TextField
                id="standard-basic"
                disabled
                label={t("user.username")}
                value={user.username != null ? user.username : ""}
              />
            </FormControl>
          </Grid>

          <Grid item md={4} sm={12} xs={12}>
            <FormControl disabled fullWidth={true}>
              <InputLabel htmlFor="gender-simple">
                {t("user.gender")}
              </InputLabel>
              <Select
                value={user.person ? user.person.gender : ""}
                onChange={(gender) => this.handleChange(gender, "gender")}
                inputProps={{
                  name: "gender",
                  id: "gender-simple",
                }}
              >
                {genders.map((item) => {
                  return (
                    <MenuItem key={item.id} value={item.id}>
                      {item.name}
                    </MenuItem>
                  );
                })}
              </Select>
            </FormControl>
          </Grid>
        </Grid>

        <Grid className="mb-10 mt-16" container spacing={3}>
          <Button
            variant="contained"
            color="primary"
            type="submit"
            className="ml-10 mr-16 btn btn-primary"
          >
            {t("Cập nhật")}
          </Button>

          <Link className="flex flex-middle" to="/user-change-password">
            <Button
              className="btn btn-secondary"
              variant="contained"
              color="secondary"
              type="button"
            >
              {t("user.change_pass")}
            </Button>
          </Link>
        </Grid>
      </div>
    );
  }
}

export default withStyles({}, { withTheme: true })(UserProfile);
