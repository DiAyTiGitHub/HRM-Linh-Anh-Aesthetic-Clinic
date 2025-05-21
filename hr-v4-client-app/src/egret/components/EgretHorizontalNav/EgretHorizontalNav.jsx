import React, { Component } from "react";
import { NavLink } from "react-router-dom";
import { Icon } from "@material-ui/core";
import localStorageService from "app/services/localStorageService";

class EgretHorizontalNav extends Component {
  constructor(props) {
    super(props);
    this.state = {
      opened: false,
      activeLink: "",
      roles:
        localStorageService
          .getLoginUser()
          ?.roles?.map((item) => item.authority) || [],
    };
    this.handleClick = this.handleClick.bind(this);
  }

  renderLevels = (levels) => {
    const { t } = this.props;
    return levels.map((item, key) => {
      if (item.isVisible) {
        if (
          !item.auth ||
          this.state.roles.some((role) => item.auth.indexOf(role) !== -1)
        ) {
          if (item.type === 2) {
            return (
              <li
                className={`nav-item ${key === this.state.activeLink ? "active" : ""
                  }`}
                key={key}
                onClick={() => this.handleClick(key)}
              >
                <div>
                  {item.icon && (
                    <Icon className="item-icon text-middle">{item.icon}</Icon>
                  )}
                  {t(item.name)}
                  {/* <Icon className="item-icon text-middle">expand_more_icon</Icon> */}
                </div>
                <div className="mega-menu">
                  <ul
                    className={`submenu-overflow ${key === this.state.activeLink ? "active" : ""
                      }`}
                  >
                    {item.children.map((item, key) => {
                      if (
                        !item.auth ||
                        this.state.roles.some(
                          (role) => item.auth.indexOf(role) !== -1
                        )
                      ) {
                        return (
                          <div className="mega-menu-item" key={key}>
                            {/* {t(item.name)} */}
                            <div className="mega-menu-sub-item">
                              {item.children?.map((subItem, subKey) => {
                                return (
                                  <div key={subKey} onClick={this.handleClick}>
                                    <NavLink
                                      className="nav-item"
                                      to={subItem.path}
                                    >
                                      {subItem.icon && (
                                        <Icon className="item-icon text-middle">
                                          {subItem.icon}
                                        </Icon>
                                      )}
                                      {t(subItem.name)}
                                    </NavLink>
                                  </div>
                                );
                              })}
                            </div>
                          </div>
                        );
                      }
                      return ''
                    })}
                  </ul>
                </div>
              </li>
            );
          }
          if (item.children) {
            return (
              <li
                className={`nav-item ${key === this.state.activeLink ? "active" : ""
                  }`}
                key={key}
                onClick={() => this.handleClick(key)}
              >
                <div>
                  {item.icon && (
                    <Icon className="item-icon text-middle">{item.icon}</Icon>
                  )}
                  {t(item.name)}
                  {/* <Icon className="item-icon text-middle">expand_more_icon</Icon> */}
                </div>
                <ul
                  className={`submenu-overflow ${key === this.state.activeLink ? "active" : ""
                    }`}
                >
                  {this.renderLevels(item.children)}
                </ul>
              </li>
            );
          } else {
            return (
              <li key={key} onClick={this.handleClick}>
                <NavLink className="nav-item" to={item.path}>
                  {item.icon && (
                    <Icon className="item-icon text-middle">{item.icon}</Icon>
                  )}
                  {t(item.name)}
                </NavLink>
              </li>
            );
          }
        }
      }
      return ''
    });
  };

  componentWillMount() {
    let user = localStorageService.getItem("auth_user");

    // console.log(user.user.roles.map((item) => item.authority))
    if (user.user) {
      this.setState({
        roles: user.user?.roles?.map((item) => item.authority) || [],
      });
    } else {
      this.setState({
        roles: user.roles?.map((item) => item.authority) || [],
      });
    }

    var currentMenu = document.querySelectorAll(".nav-item.active")[0];
    if (currentMenu) {
      currentMenu.parentElement.parentElement.parentElement.classList.add(
        "active"
      );
      // console.log("add active")
    }
  }

  UNSAFE_componentWillUpdate(nextProps, nextState) {
    var currentMenu = document.querySelectorAll(".nav-item.active")[0];
    if (currentMenu) {
      if (nextState.activeLink !== this.state.activeLink) {
        currentMenu.classList.remove("active");
      } else {
        if (currentMenu) {
          currentMenu.parentElement.parentElement.parentElement.classList.add(
            "active"
          );
          // console.log("add active")
        }
      }
    }
  }

  handleClick = (key) => {
    if (key) {
      this.setState({ activeLink: key, opened: true });
    }
  };

  render() {
    let max = this.props.max;
    let navigation = this.props.navigation;
    if (max && navigation.length > max) {
      let childItem = {
        name: "More",
        icon: "more_vert",
        children: navigation.slice(max, navigation.length),
      };
      navigation = navigation.slice(0, max);
      navigation.push(childItem);
    }
    return (
      <div className="horizontal-nav">
        <ul ref={this.wrapperRef} className="menu">
          {this.renderLevels(navigation)}
        </ul>
      </div>
    );
  }
}
export default EgretHorizontalNav;
