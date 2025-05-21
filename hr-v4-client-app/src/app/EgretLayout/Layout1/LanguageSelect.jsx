import React from "react";
import PropTypes from "prop-types";
import { withStyles } from "@material-ui/core/styles";
import Input from "@material-ui/core/Input";
import InputLabel from "@material-ui/core/InputLabel";
import MenuItem from "@material-ui/core/MenuItem";
import FormHelperText from "@material-ui/core/FormHelperText";
import FormControl from "@material-ui/core/FormControl";
import Select from "@material-ui/core/Select";
import { Popper } from "@material-ui/core";

const styles = (theme) => ({
  root: {
    display: "flex",
    flexWrap: "wrap",
    alignItems: "center",
  },
  formControl: {
    margin: theme.spacing(1),
    marginTop: 0,
    marginBottom: 0,
    minWidth: 120,
    backgroundColor: 'inherit',
  },
  selectEmpty: {
    marginTop: theme.spacing(2),
  },
  languageSelect: {
    height: '100%',
    color: '#B6D0E7',
    "&::before": {
      display: "none",
    },
    "&::after": {
      display: "none",
    },
    "& > svg": {
      color: "white",
    }
  },
});

class LanguageSelect extends React.Component {
  state = {
    selected: "vi",
    hasError: false,
  };

  handleChange(value) {
    //alert(t('description.part1'));
    const { i18n } = this.props;
    i18n.changeLanguage(value);
    this.setState({ selected: value });
  }
  render() {
    const { t, classes } = this.props;
    const { selected, hasError } = this.state;

    const { horizontalPosition = "left" } = this.props;

    // let language= 'vi';
    // const changeLanguage = lng => {
    //   i18n.changeLanguage(lng);
    // };
    return (
      <form className={`${classes.root} nav-item-left language-select`} autoComplete="off">
        <FormControl className={classes.formControl} error={hasError}>
          <Select
            name="name"
            className={classes.languageSelect}
            value={selected}
            onChange={(event) => this.handleChange(event.target.value)}
            input={<Input id="name" className={classes.languageInput} />}
            MenuProps={{
              anchorOrigin: {
                vertical: "bottom",
                horizontal: horizontalPosition
              },
              transformOrigin: {
                vertical: "top",
                horizontal: horizontalPosition
              },
              getContentAnchorEl: null,
              PaperProps: {
                style: {
                  width: 175,
                  transform: "translateY(calc(var(--topbar-height) - 39px))"
                },
              }
            }}
          >
            <MenuItem value="vi">
              <div className="flex">
                <img src="/assets/images/flags/vn.png" alt="" />
                <span className="ml-4">
                  Tiếng Việt
                </span>
              </div>
            </MenuItem>
            {/* <MenuItem value="de">Deutsch</MenuItem> */}
            <MenuItem value="en">

              <div className="flex">
                <img src="/assets/images/flags/us.png" alt="" />
                <span className="ml-4">
                  English
                </span>
              </div>
            </MenuItem>
          </Select>
          {hasError && <FormHelperText>This is required!</FormHelperText>}
        </FormControl>
      </form>
    );
  }
}

LanguageSelect.propTypes = {
  classes: PropTypes.object.isRequired,
};

export default withStyles(styles)(LanguageSelect);
