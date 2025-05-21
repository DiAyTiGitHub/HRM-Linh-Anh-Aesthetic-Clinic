import React from "react";
import { makeStyles } from "@material-ui/core/styles";
import { useTranslation } from "react-i18next";
import GlobitsTextField from "../../common/form/GlobitsTextField";
import { Grid, } from "@material-ui/core";

const useStyles = makeStyles ((theme) => ({
  root:{
    width:"100%",
  },
  container:{
    background:"rgb(237, 245, 251)",
    boxShadow:"0 0.5rem 1rem rgb(0 0 0, 15%)",
  },
  details:{
    background:"#fff",
  },
  heading:{
    fontSize:theme.typography.pxToRem (15),
    flexBasis:"33.33%",
    flexShrink:0,
  },
  secondaryHeading:{
    fontSize:theme.typography.pxToRem (15),
    color:theme.palette.text.secondary,
  },
}));

export default function ChangePassWordAccordion ({readOnly}) {

  const classes = useStyles ();
  const {t} = useTranslation ();


  return (
      <Grid item md={12} xs={12}>
        <div className={classes.root}>
          <Grid container spacing={2}>
            <Grid item sm={6} xs={12}>
              <GlobitsTextField
                  type="password"
                  label={t ("user.password")}
                  validate
                  name="password"
                  readOnly={readOnly}/>
            </Grid>
            <Grid item sm={6} xs={12}>
              <GlobitsTextField
                  type="password"
                  validate
                  label={t ("user.rePassword")}
                  name="confirmPassword"
                  readOnly={readOnly}/>
            </Grid>
          </Grid>
        </div>
      </Grid>
  );
}
