import { Button, ButtonGroup, Grid, Popover, useMediaQuery, useTheme } from "@material-ui/core";
import GlobitsBreadcrumb from "app/common/GlobitsBreadcrumb";
import React from "react";
import { useTranslation } from "react-i18next";
import AddIcon from "@material-ui/icons/Add";
import DeleteOutlineIcon from "@material-ui/icons/DeleteOutline";
import DashboardIcon from '@material-ui/icons/Dashboard';
import PublishIcon from "@material-ui/icons/Publish";
import AutorenewIcon from '@material-ui/icons/Autorenew';
import InsuranceTypeList from "./InsuranceTypeList";


const InsuranceTypeIndex = () => {
  const { t } = useTranslation();
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down("sm"));
  const [anchorEl, setAnchorEl] = React.useState(null);

  const open = Boolean(anchorEl);
  const id = open ? "simple-popper" : undefined;
  const handleClick = (event) => {
    setAnchorEl(anchorEl ? null : event.currentTarget);
  };
  return (
    <div className="content-index">
      <div className="index-breadcrumb">
        <GlobitsBreadcrumb
          routeSegments={[
            { name: t("navigation.insurance.root") },
            { name: t("navigation.insurance.insuranceType") }
          ]}
        />
      </div>

      <Grid className="" container spacing={2}>
        <Grid item lg={6} md={6} sm={4} xs={3}>
          <ButtonGroup
            color="container"
            aria-label="outlined primary button group"
          >
            <Button
              startIcon={<AddIcon />}
              onClick={() => {
                // handleEditUser();
              }}
            >
              {!isMobile && "Thêm mới"}
            </Button>
            <Button
            //   disabled={listSelected?.length === 0}
              startIcon={<DeleteOutlineIcon />}
            >
              {!isMobile && t("general.button.delete")}
            </Button>
            <Button startIcon={<DashboardIcon />} aria-describedby={id} type="button" onClick={handleClick}>
              Khác
            </Button>
            <Popover
              id={id}
              open={open}
              anchorEl={anchorEl}
              onClose={handleClick}
              anchorOrigin={{
                vertical: 'bottom',
                horizontal: 'right',
              }}
              transformOrigin={{
                vertical: 'top',
                horizontal: 'right',
              }}
            >
              <div className="menu-list-button">
                <div className="menu-item-button">
                  <PublishIcon
                    style={{ fontSize: 16, transform: "rotate(180deg)" }}
                  />{" "}
                  Kết xuất danh sách
                </div>
                <div className="menu-item-button">
                  <PublishIcon style={{ fontSize: 16 }} /> Import dữ liệu
                </div>
              </div>
            </Popover>
          </ButtonGroup>
        </Grid>
       
        <Grid item xs={12}>
          <InsuranceTypeList />
        </Grid>
      </Grid>
    </div>
  );
};

export default InsuranceTypeIndex;
