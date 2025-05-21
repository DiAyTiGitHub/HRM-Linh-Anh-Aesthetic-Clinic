import React, { Component } from "react";
import { Grid } from "@material-ui/core";
import { getUserByUsername, } from "./UserService";
import { toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import { Breadcrumb } from "egret";
import UserProfileForm from './UserProfileForm';

toast.configure({
  autoClose: 2000,
  draggable: false,
  limit: 3
});

class UserProfile extends Component {
  state = {
    isAddNew: false,
    listRole: [],
    roles: [],
    active: true,
    email: "",
    person: {},
    username: "",
    org: {},
    changePass: true,
    password: "",
    confirmPassword: "",
  };

  listGender = [
    { id: "M", name: "Nam" },
    { id: "F", name: "Nữ" },
    { id: "U", name: "Không rõ" },
  ];

  handleChange = (event, source) => {
    event.persist();
    if (source === "switch") {
      this.setState({ isActive: event.target.checked });
      return;
    }
    if (source === "changePass") {
      this.setState({ changePass: event.target.checked });
      return;
    }
    if (source === "active") {
      this.setState({ active: event.target.checked });
      return;
    }
    if (source === "displayName") {
      let { person } = this.state;
      person = person ? person : {};
      person.displayName = event.target.value;
      this.setState({ person: person });
      return;
    }
    if (source === "gender") {
      let { person } = this.state;
      person = person ? person : {};
      person.gender = event.target.value;
      this.setState({ person: person });
      return;
    }
    this.setState({
      [event.target.name]: event.target.value,
    });
  };

  handleChangePassWord = (password) => {
    this.setState({
      password: password,
      changePass: true
    }, () => {
    })
  }

  handleFormSubmit = (values) => {
    console.log(values)
    let { t } = this.props
    let { id, user } = this.state;
    let userOrg = {};
    if (user == null) {
      user = {};
    }
    user.username = values.username;
    user.email = values.email;
    user.person = values.person;
    user.person.displayName = values.displayName;
    user.person.gender = values.gender;
    user.roles = values.roles;
    userOrg.user = user;
    userOrg.org = values.org;
    userOrg.id = id;
    getUserByUsername(this.state.username).then((data) => {
      if (data.data && data.data.id) {
        if (!user.id || (id && data.data.id !== user.id)) {
          toast.warning(t('toast.user_exist'));
          return;
        }
      }
    });
  };

  selectRoles = (rolesSelected) => {
    this.setState({ roles: rolesSelected }, function () { });
  };

  selectHealthOrganization = (event, labTest) => {
    this.setState({ org: labTest }, function () { });
  };
  render() {
    let { t } = this.props;
    let {
      listRole,
      roles,
      email,
      person,
      username,
    } = this.state;
    return (
      <div className="m-sm-30">
        <div className="mb-sm-30">
          <Breadcrumb routeSegments={[
            { name: t('navigation.administration.user') },
            { name: t('navigation.administration.personalInfo') }
          ]} />
        </div>

        <Grid container>
          <Grid item md={12}>
            <UserProfileForm
              initialValues={{
                person: person ? person : {},
                email: email ? email : "",
                username: username ? username : "",
                roles: roles ? roles : [],
                displayName: person.displayName ? person.displayName : "",
                gender: person.gender ? person.gender : {},
                org: this.state.org ? this.state.org : null
              }}
              handleSubmit={this.handleFormSubmit}
              listRole={listRole}
              listGender={this.listGender}
              listOrg={this.state.listOrg}
            />
          </Grid>
        </Grid>
      </div>
    );
  }
}

export default UserProfile;
