import { Grid } from "@material-ui/core";
import React from "react";
import { Breadcrumb } from "egret";
import axios from "axios";
import ConstantList from "../../appConfig";
import "react-image-crop/dist/ReactCrop.css";
import JwtAuthService from "../../services/jwtAuthService";
import { toast } from "react-toastify";
import ChangePasswordForm from "./ChangePasswordForm";

toast.configure({
  autoClose: 2000,
  draggable: false,
  limit: 3,
});

class ChangePassword extends React.Component {
  state = {
    oldPassword: "",
    password: "",
    confirmPassword: "",
  };

  handleFormSubmit = async (values) => {
    const { t } = this.props;
    const { user } = this.state;
    user.changePass = true;
    user.password = values.password;
    user.oldPassword = values.oldPassword;
    user.confirmPassword = values.confirmPassword;
    const url = ConstantList.API_ENPOINT + "/api/user-ext/reset-password";
    let isChangedOK = false;

    await axios
      .put(url, user)
      .then((response) => {
        toast.success(t("toast.changePassword"));
        console.log(response);
        isChangedOK = true;
      })
      .catch((err) => {
        console.log(err);
        toast.error(t("toast.change_password_failure"));
      });
    if (isChangedOK) {
      await JwtAuthService.logout();
    }
  };

  render() {
    const { t } = this.props;
    const { oldPassword, password, confirmPassword, user } = this.state;
    return (
      <div className="m-sm-30">
        <div className="mb-sm-30">
          <Breadcrumb
            routeSegments={[
              { name: t("navigation.administration.user") },
              { name: t("user.changePass") },
            ]}
          />
        </div>
        <Grid container>
          <Grid item md={12}>
            <ChangePasswordForm
              initialValues={{
                oldPassword: oldPassword ? oldPassword : "",
                password: password ? password : "",
                confirmPassword: confirmPassword ? confirmPassword : "",
                user: user ? user : {}
              }}
              handleSubmit={this.handleFormSubmit}
            />
          </Grid>
        </Grid>
      </div>
    );
  }
}
export default ChangePassword;
