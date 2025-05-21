import { Collapse, Grid, makeStyles } from "@material-ui/core";
import {
  BookmarkRounded,
  SettingsRounded,
  HomeRounded,
  BusinessRounded
} from "@material-ui/icons";
import Config from "app/appConfig";
import localStorageService from "app/services/localStorageService";
import { observer } from "mobx-react";
import { memo } from "react";
import { NavLink } from "react-router-dom/cjs/react-router-dom.min";


const useStyles = makeStyles((theme) => ({
  gridFlex: {
    "& h5": {
      fontSize: "1rem",
      color: "#5899d1 !important",
      display: "flex",
      alignItems: "center ",
      "& svg": {
        fill: "#888a8d",
        marginRight: "10px",
        fontSize: "2rem !important",
      },
    },
  },

  content: {
    maxHeight: "95vh",
    overflow: "auto",
    zIndex: "999 !important",
  },
}));


const moduleLinkOptions = [
  {
    name: "EMR",
    path: "https://emr.smarthospital247.com/",
    icon: <HomeRounded />,
    color: "#FF4B55",
    code: "/store",
    isLink: true
  },
  {
    name: "IC",
    path: "https://hrlinhanh.smarthospital247.com/",
    icon: <BusinessRounded />,
    color: "#D68B5F",
    code: "/store",
    isLink: true
  },
  {
    name: "CRM",
    path: "https://crm.smarthospital247.com/",
    icon: <SettingsRounded />,
    color: "#EBD422",
    code: "/store",
    isLink: true
  },
];

function ModuleOptionsPopup(props) {
  const {
    openChooseModulePopup,
  } = props;

  const classes = useStyles();

  const handleOptionClick = (option) => {

    localStorageService.updateShortcutAccess(option);
    return props.setState()
  };

  const user = localStorageService.getLoginUser();

  return (
    <Collapse in={openChooseModulePopup} className={`moduleOptionPopup`}>
      <Grid container spacing={2} className={classes.content}>
        {
          moduleLinkOptions
            ?.map(function (option, index) {
              const {
                path,
                isLink,
                icon,
                name,
                color,
              } = option;

              return (
                <Grid item xs={6} sm={3} key={index}>
                  {
                    isLink ? (
                      <a
                        href={path}
                        target="_blank"
                        className="moduleOptionWrapper"
                      >
                        <div
                          style={{ backgroundColor: color }}
                          className="moduleOptionBtn"
                        >
                          {icon}
                        </div>
                        <h5 className="optionTitle">{name}</h5>
                      </a>
                    ) : (
                      <NavLink
                        to={Config.ROOT_PATH + path}
                        className="moduleOptionWrapper"
                        onClick={() => {
                          handleOptionClick(option);
                        }}
                      >
                        <div
                          className="moduleOptionBtn"
                          style={{ backgroundColor: color }}
                        >
                          {icon}
                        </div>
                        <h5 className="optionTitle">{name}</h5>
                      </NavLink>
                    )
                  }
                </Grid>
              );
            })
        }

      </Grid>
    </Collapse>
  );
}

export default memo(observer(ModuleOptionsPopup));