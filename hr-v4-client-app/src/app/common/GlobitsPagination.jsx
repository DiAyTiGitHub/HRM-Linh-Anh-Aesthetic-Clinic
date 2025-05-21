import React, { useEffect, useState } from "react";
import {
  TextField,
  MenuItem,
  Grid,
  makeStyles,
  Button,
} from "@material-ui/core";
import AddIcon from "@material-ui/icons/Add";
import Pagination from "@material-ui/lab/Pagination";
import { useTranslation } from "react-i18next";
import { useTheme } from "@material-ui/core/styles";
import useMediaQuery from "@material-ui/core/useMediaQuery";
import PaginationOptionPopup from "./PaginationOptionPopup";
import PropTypes from "prop-types";

const useStyles = makeStyles((theme) => ({
  paginationBar: {
    display: "flex",
    flexWrap: "nowrap",
    padding: "15px 7px",
    [theme.breakpoints.down("xs")]: {
      padding: "14px 0px",
    },
    float: "right",
    "& > .Mui-selected": {
      color: "#fff",
      backgroundColor: "#2a80c8",
      borderColor: "#2a80c8",
    },
    "& > .Mui-selected::hover": {
      color: "#fff",
      backgroundColor: "#1f5f94",
      borderColor: "#1f5f94",
    },
  },
  rowTool: {
    display: "flex",
    // width: "100%",
    "& > p": {
      margin: "5px 0px",
    },
  },
  rowOptions: {
    marginLeft: "15px",
    "& > div::before": {
      content: "none",
    },
    "& > div::after": {
      content: "none",
    },
  },
  totalRows: {
    "& > p": {
      margin: 0,
      paddingTop: "3px",
      "& > span": {
        margin: "0px 12px 0px 6px",
        padding: "0px 12px 0px 6px",
        borderRight: "1px solid rgba(0, 0, 0, 0.15)",
        [theme.breakpoints.down("sm")]: {
          borderRight: "none",
        },
      },
    },
    [theme.breakpoints.down("sm")]: {
      width: "100%",
    },
  },
  pageGoto: {
    display: "flex",
    "& > p": {
      margin: 0,
      marginRight: "15px",
      paddingTop: "3px",
      "& > span": {
        margin: "0px 12px 0px 6px",
        borderRight: "1px solid rgba(0, 0, 0, 0.15)",
      },
    },
  },
  gotoInput: {
    width: "50px",
    "& > div::before": {
      content: "none",
    },
    "& > div::after": {
      content: "none",
    },
  },
  rowsPerPage: {
    display: "flex",
    "& > p": {
      margin: 0,
      paddingTop: "3px",
    },

    // "& .MuiSelect-icon": {
    //   right: "-50%",
    // },
  },
  pageSelector: {
    "& .MuiPagination-ul": {
      "& .MuiPaginationItem-root": {
        [theme.breakpoints.down("sm")]: {
          minWidth: "26px",
          margin: 0,
          padding: 0,
        },
      },
    },
  },
  pagingElement: {
    "& > ul": {
      justifyContent: "space-evenly",
    },

    "& .MuiButtonBase-root": {
      backgroundColor: "#5899ca !important",
    },
  },
}));

function GlobitsPagination(props) {
  const { t } = useTranslation();
  const classes = useStyles();
  let {
    handleChangePage,
    totalPages,
    setRowsPerPage,
    pageSizeOption,
    totalElements,
    page,
  } = props;
  
  const [pageSize, setPageSize] = React.useState(props.pageSize);
  const handleChange = (event) => {
    setRowsPerPage(event);
    setPageSize(event.target.value);
  };

  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down("xs"));

  const [openPopup, setOpenPopup] = useState(false);

  useEffect(() => {
    setPageSize(props.pageSize)
  }, [props.pageSize]);

  return (
    <div className={classes.paginationBar}>
      <Grid container spacing={2}>
        {!isMobile && (
          <Grid className={classes.rowTool} item>
            <div className={classes.totalRows}>
              <p>
                {t("general.totalRows")}
                <span className={classes.totalRowsNum}>{totalElements}</span>
              </p>
            </div>
            <div className={classes.rowsPerPage}>
              <p>{t("general.rowsPerPage")}</p>
              <TextField
                select
                value={pageSize}
                className={classes.rowOptions}
                onChange={handleChange}
              >
                {pageSizeOption.map((option, index) => {
                  return (
                    <MenuItem key={index} value={option}>
                      {option}
                    </MenuItem>
                  );
                })}
              </TextField>
            </div>
            {/* <div className={classes.pageGoto}>

              <p>
                <span></span>
                {t("general.goto_page")}
              </p>
              <TextField
                className={classes.gotoInput}
                type="number"
                name="pageIndex"
                value={pageIndex}
                onChange={(e) => setPageIndex(e.target.value)}
                onKeyDown={(e) => {
                  if (e.key === "Enter") {
                    handleGo(e);
                  }
                }}
              />
            </div> */}
          </Grid>
        )}

        {isMobile && (
          <>
            <Grid className={classes.rowTool} item xs={12}>
              <div className={classes.totalRows}>
                <p>
                  {t("general.totalRows")}
                  <span className={classes.totalRowsNum}>{totalElements}</span>
                </p>
              </div>
              <Button
                className="btn btn-primary d-inline-flex"
                startIcon={<AddIcon />}
                variant="contained"
                onClick={() => setOpenPopup(true)}
              />
            </Grid>

            <PaginationOptionPopup
              open={openPopup}
              handleClose={() => setOpenPopup(false)}
              totalElements={totalElements}
              setRowsPerPage={setRowsPerPage}
              pageSizeOption={pageSizeOption}
              totalPages={totalPages}
              handleChangePage={handleChangePage}
              page={page}
              pageSize={pageSize}
            />
          </>
        )}

        {isMobile && (
          <Grid className={classes.pageSelector} item xs={12}>
            <Pagination
              className={classes.pagingElement}
              count={totalPages}
              shape="rounded"
              page={page}
              color="primary"
              onChange={handleChangePage}
              boundaryCount={1}
              siblingCount={1}
              showFirstButton
              showLastButton
            />
          </Grid>
        )}

        {!isMobile && (
          <Grid className={classes.pageSelector} item>
            <Pagination
              count={totalPages}
              shape="rounded"
              page={page}
              color="primary"
              onChange={handleChangePage}
              boundaryCount={1}
              siblingCount={1}
              showFirstButton
              showLastButton
            />
          </Grid>
        )}
      </Grid>
    </div>
  );
}

GlobitsPagination.propTypes = {
  totalElements: PropTypes.number,
  totalPages: PropTypes.number,
  pageSizeOption: PropTypes.arrayOf(PropTypes.number),
  setRowsPerPage: PropTypes.func,
  handleChangePage: PropTypes.func,
}

GlobitsPagination.defaultProps = {
  pageSizeOption: [5, 10, 25, 50],
}

export default GlobitsPagination