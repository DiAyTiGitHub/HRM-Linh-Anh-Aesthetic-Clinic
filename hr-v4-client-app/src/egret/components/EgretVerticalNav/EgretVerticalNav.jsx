import React, { Component } from "react";
import { NavLink } from "react-router-dom";
import { Icon } from "@material-ui/core";
import TouchRipple from "@material-ui/core/ButtonBase";
import EgretVerticalNavExpansionPanel from "./EgretVerticalNavExpansionPanel";
import { withStyles } from "@material-ui/styles";
import { withTranslation } from "react-i18next";
import localStorageService from "app/services/localStorageService";
import { withRouter } from "react-router-dom";

const ViewEgretVerticalNavExpansionPanel = withTranslation()(
  EgretVerticalNavExpansionPanel
);

const styles = (theme) => ({
  expandIcon: {
    transition: "transform 125ms cubic-bezier(0, 0, 0.2, 1) 0ms",
    transform: "rotate(90deg)",
  },
  collapseIcon: {
    transition: "transform 125ms cubic-bezier(0, 0, 0.2, 1) 0ms",
    transform: "rotate(0deg)",
  },
});

class EgretVerticalNav extends Component {
  state = {
    collapsed: false,
    roles:
      localStorageService
        .getLoginUser()
        ?.roles?.map((item) => item.authority) || [],
  };

  checkIsContainsActiveChild = (item) => {
    if (!item) return false;
    if (String(this.props?.location?.pathname) === (item?.path)) {
      return true;
    }
    if (item?.children && item?.children?.length > 0) {
      const isContainsActiveChild = item?.children?.some(child => {
        const isContainsActiveChild = this.checkIsContainsActiveChild(child);
        if (isContainsActiveChild) return true;
        return false;
      });

      if (isContainsActiveChild) return true;
    }
    return false;
  }

  renderLevels = (data, menuLevel) => {
    const { t } = this.props;

    const isFirstLevelMenu = menuLevel === 1;

    let paddingForSubMenu = (menuLevel) * 16;
    if (isFirstLevelMenu) paddingForSubMenu = 8;

    return (
      <>
        {
          (!data || data.length == 0) && isFirstLevelMenu && (
            <div className="flex justify-center align-center">
              <p className="m-0 p-0 text-white">
                Không có kết quả
              </p>
            </div>
          )
        }

        {data && data?.length > 0 && data?.map((item, index) => {

          if (item.isVisible) {
            if (
              !item.auth ||
              this.state.roles.some((role) => item.auth.indexOf(role) !== -1)
            ) {
              if (item?.children && item?.children?.length > 0) {
                if (item?.type === 2) {
                  let newNav = [];
                  item.children.map((subItem, i) => newNav.push(...subItem?.children));

                  return (
                    <ViewEgretVerticalNavExpansionPanel
                      item={item}
                      key={index}
                      children={item.children}
                      menuLevel={menuLevel}
                    >
                      {this.renderLevels(newNav, menuLevel + 1)}
                    </ViewEgretVerticalNavExpansionPanel>
                  );
                } else {
                  return (
                    <ViewEgretVerticalNavExpansionPanel
                      item={item}
                      key={index}
                      listLinkChildren={item?.children}
                      menuLevel={menuLevel}
                    >
                      {this.renderLevels(item?.children, menuLevel + 1)}
                    </ViewEgretVerticalNavExpansionPanel>
                  );
                }
              } else {
                const path = item?.path;

                // if (item.name === 'navigation.work.task') {
                //   path += '/' + (JSON.parse(localStorage.getItem('id_project_task')) || 'all-project')
                // }

                const isCurrentlyActive = this.props?.location?.pathname === path;

                let isContainsActiveChild = this.checkIsContainsActiveChild(this?.props?.item);
                if (isFirstLevelMenu) {
                  // if (!isContainsActiveChild) {
                  //   this.setState({
                  //     collapsed: false,
                  //   });
                  // }
                }

                return (
                  <div key={index}>
                    {/* <NavLink key={index} to={path} className="nav-item "> */}
                    <TouchRipple key={item.name} name="child" 
                      // component={NavLink}
                      // to={path}
                      className={`nav-item flex-middle h-48 w-100
                        pl-${paddingForSubMenu}
                      ${(isCurrentlyActive || isContainsActiveChild) ? "active" : ""} 
                      ${isFirstLevelMenu ? "isFirstLevelMenu" : ""}
                      `}
                      //onClick={() => this.props.history.push(path)}
                    >
                      <a
                        href={path}
                        target={item?.target === "_blank" ? "_blank" : "_self"}
                        rel="noopener noreferrer"
                        className="nav-link flex w-100 h-100 items-center"
                        onClick={(e) => {
                          if (item?.target !== "_blank") {
                            e.preventDefault();
                            this.props.history.push(path);
                          }
                        }}
                      >
                        {item?.icon && (
                          <Icon className="item-icon text-middle">{item?.icon}</Icon>
                        )}
                        <span className="text-align-left pl-11 item-text">{t(item?.name)}</span>
                        {item?.badge && (
                          <div className={`badge bg-${item?.badge?.color}`}>
                            {item?.badge?.value}
                          </div>
                        )}
                      </a>
                    </TouchRipple>
                    {/* </NavLink> */}
                  </div>
                );
              }
            }
          }
          return ''
        })}
      </>
    );
  };

  componentDidMount() {
    let user = localStorageService.getItem("auth_user");

    if (user?.user) {
      this.setState({
        roles: user?.user?.roles?.map((item) => item.authority) || [],
      });
    } else {
      this.setState({
        roles: user?.roles?.map((item) => item.authority) || [],
      });
    }
  }

  render() {
    //FIRST LEVEL MENU => HAS SMALL WHITE CURSOR IN THE RIGHT
    const menuLevel = 1;

    // console.log("checkign onRenderNavigations: ", this?.props?.onRenderNavigations);

    return (
      <div className="navigation">
        {this.renderLevels(this.props.onRenderNavigations, menuLevel)}
      </div>
    );
  }
}

export default withRouter(withStyles(styles)(EgretVerticalNav));
