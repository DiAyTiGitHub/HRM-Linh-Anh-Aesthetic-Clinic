import React, { Component } from "react";
import ConstantList from "app/appConfig";
import history from "history.js";

class Brand extends Component {
  state = {};

  render () {
    return (
        <a
            href="#"
            onClick={(e) => {
              e.preventDefault ();
              history.push (ConstantList.HOME_PAGE);
            }}
            className="flex flex-middle flex-space-between brand-area" target="_blank"
            rel="noopener noreferrer">
          <div className="flex flex-middle brand w-100 flex-center ">
            <img
                src={ConstantList.ROOT_PATH + "assets/images/logo.png"}
                alt="company-logo"
                className="sidebarLogoImage"
                // style={{backgroundColor:"#ffffff"}}
            />
          </div>
          {this.props.children}
        </a>
    );
  }
}

export default Brand;
