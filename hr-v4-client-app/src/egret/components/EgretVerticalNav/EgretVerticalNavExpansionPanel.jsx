import React, { Component } from "react";
import { withStyles, Icon } from "@material-ui/core";
import TouchRipple from "@material-ui/core/ButtonBase";
import { withRouter } from "react-router-dom";

const styles = (theme) => {
  return {
    expandIcon: {
      transition: "transform 0.3s cubic-bezier(0, 0, 0.2, 1) 0ms",
      transform: "rotate(90deg)",
      // marginRight: "16px"
    },
    collapseIcon: {
      transition: "transform 0.3s cubic-bezier(0, 0, 0.2, 1) 0ms",
      transform: "rotate(180deg)",
      // marginRight: "16px"
    },
    "expansion-panel": {
      overflow: "hidden",
      transition: "max-height 0.1s cubic-bezier(0, 0, 0.2, 1)",
    },
    highlight: {
      background: theme.palette.primary.main,
    },
  };
};

class EgretVerticalNavExpansionPanel extends Component {
  state = {
    collapsed: true,
  };
  elementRef = React.createRef();

  componentHeight = 0;

  handleClick = () => {
    this.setState({ collapsed: !this.state.collapsed });
  };

  calcaulateHeight(node) {
    if (node.name !== "child") {
      for (let child of node?.children) {
        this.calcaulateHeight(child);
      }
    }
    this.componentHeight += node?.clientHeight;
    return;
  }

  componentDidMount() {
    let { location } = this.props;
    // this.calcaulateHeight(this.elementRef);

    // OPEN DROPDOWN IF CHILD IS ACTIVE
    for (let child of this?.elementRef?.children) {
      if (child.getAttribute("href") === location?.pathname) {
        this.setState({ collapsed: false });
      }
    }
  }

  // componentDidUpdate() {
  //   console.log("componentDidUpdate called");
  //   let { location } = this.props;

  //   let { collapsed } = this.state;
  //   let { classes, children, isFirstLevelMenu } = this.props;
  //   console.log("location: ", location);

  //   if (children && children.length > 0) {
  //     const isChildrenCalled = children?.some(function (child) {
  //       return child?.path == location?.pathname;
  //     });

  //     console.log("isChildrenCalled", isChildrenCalled);
  //   }
  // }

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

  render() {
    let { collapsed } = this.state;
    let { classes, children, menuLevel } = this.props;
    let { name, icon, badge, color, iconText } = this.props.item;
    const { t } = this.props;

    const isFirstLevelMenu = menuLevel === 1;

    let paddingForSubMenu = (menuLevel) * 16;

    let isContainsActiveChild = this.checkIsContainsActiveChild(this?.props?.item);

    if (isFirstLevelMenu) {
      paddingForSubMenu = 8;
      // if (!isContainsActiveChild) {
      //   this.setState({
      //     collapsed: false,
      //   });
      // }
    }


    return (
      <div >
        <TouchRipple
          className={`nav-item flex-middle h-48 w-100 
            pl-${paddingForSubMenu}
            ${isContainsActiveChild ? "active" : ""} 
            ${isFirstLevelMenu ? "isFirstLevelMenu" : ""}
            `}
          onClick={this.handleClick}
        >
          <div>
            {(() => {
              if (isFirstLevelMenu && icon) {
                return (
                  <Icon className="item-icon text-middle"
                  // style={{ backgroundColor: item.color ? item.color : '' }}
                  >
                    {icon}
                  </Icon>
                );
              }

              return (
                <>
                  {iconText && (
                    <span className="item-icon icon-text">
                      {iconText}
                    </span>
                  )}
                </>
              );

            })()}

            <span className="text-middle pl-20 item-text">{t(name)}</span>
          </div>
          {badge && (
            <div className={`badge bg-${badge?.color}`}>{badge?.value}</div>
          )}
          <div
            className={
              collapsed
                ? classes?.collapseIcon + " item-arrow"
                : classes?.expandIcon + " item-arrow"
            }
          >
            <Icon className="text-middle">chevron_right</Icon>
          </div>
        </TouchRipple>

        <div
          ref={(el) => (this.elementRef = el)}
          className={`submenu ${classes["expansion-panel"]} `}
          style={
            collapsed
              ? { maxHeight: "0px" }
              :
              // { maxHeight: this.componentHeight + "px" }
              { maxHeight: "100%" }
          }
        >
          {children}
        </div>
      </div>
    );
  }
}

export default withRouter(withStyles(styles)(EgretVerticalNavExpansionPanel));
