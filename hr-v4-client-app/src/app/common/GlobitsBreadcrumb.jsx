import React, { Fragment } from "react";
import { makeStyles } from "@material-ui/core";
import { NavLink } from "react-router-dom";
import ConstantList from "../appConfig";
import { useTranslation } from "react-i18next";

const useStyles = makeStyles((theme) => ({
  root: {
    display: "flex",
    alignItems: "center",
    justifyContent: "space-between",
  },
  title: {
    display: "flex",
    fontSize: "18px",
    padding: "10px 0",
  },
  location: {
    display: "flex",
    fontSize: "12px",
  },
  rightArrow: {
    borderRight: '1px solid #ced4da',
    borderBottom: '1px solid #ced4da',
    width: '6px',
    height: '6px',
    margin: '5px',
    transform: 'rotate(-45deg)',
  },
  currentPage: {
    color: "#01c0c8",
    fontWeight: 500
  }
}));

const GlobitsBreadcrumb = ({ routeSegments, noRight }) => {
  const classes = useStyles();
  const { t } = useTranslation();
  return (
    <div className={classes.root}>
      <div className={classes.title}>
        {(!noRight && routeSegments) ? (
          <span style={{ color: "#717276", textTransform: 'uppercase' }}>
            {routeSegments[routeSegments.length - 1]["subName"] ? routeSegments[routeSegments.length - 1]["subName"] : routeSegments[routeSegments.length - 1]["name"]}
          </span>
        ) : null}
      </div>
      <div className={classes.location}>
        <NavLink to={ConstantList.ROOT_PATH}>{t("general.home")}</NavLink>
        {routeSegments
          ? routeSegments.map((route, index) => (
            <Fragment key={index}>
              <span className={classes.rightArrow}></span>

              <span className={classes.currentPage}>{route.name}</span>

              {route.subName &&
                <>
                  <span>&nbsp; &nbsp;/&nbsp; &nbsp;</span>
                  <span className="">{route.subName}</span>
                </>
              }

            </Fragment>
          ))
          : null}
      </div>
    </div>
  );
};

export default GlobitsBreadcrumb;
