import React, { Component } from "react";
import { Icon, IconButton, Hidden } from "@material-ui/core";
import { withStyles } from "@material-ui/core";

const styles = (theme) => ({
  root: {
    display: "block",
    [theme.breakpoints.down("sm")]: {
      display: "none",
    },
  },
});

class EgretToolbarMenu extends Component {
  state = {
    open: false,
  };

  handleToggle = () => {
    this.setState({ open: !this.state.open });
  };

  render() {
    let { offsetTop, children } = this.props;
    const { classes } = this.props;

    return (
      <div
        // className={classList({
        //   "toolbar-menu-wrap": true,
        //   open: this.state.open,
        // })}
        className={classes.root}
      >
        <Hidden mdUp>
          <IconButton onClick={this.handleToggle}>
            <Icon>{this.state.open ? "close" : "more_vert"}</Icon>
          </IconButton>
        </Hidden>

        <div
          style={{ top: offsetTop }}
          className="flex flex-middle menu-area container"
        >
          {children}
        </div>
      </div>
    );
  }
}

export default withStyles(styles)(EgretToolbarMenu);
