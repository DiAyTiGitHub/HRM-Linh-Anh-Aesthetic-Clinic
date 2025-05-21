import {
  Button,
  ButtonGroup,
  Grid,
  Popover,
  useMediaQuery,
  useTheme,
} from "@material-ui/core";
import GlobitsBreadcrumb from "app/common/GlobitsBreadcrumb";
import React from "react";
import { useTranslation } from "react-i18next";
import AddIcon from "@material-ui/icons/Add";
import DeleteOutlineIcon from "@material-ui/icons/DeleteOutline";
import DashboardIcon from "@material-ui/icons/Dashboard";
import PublishIcon from "@material-ui/icons/Publish";
import CheckCircleOutlineIcon from '@material-ui/icons/CheckCircleOutline';
import BlockIcon from '@material-ui/icons/Block';
import HighlightOffIcon from '@material-ui/icons/HighlightOff';
import SearchIcon from '@material-ui/icons/Search';
import HolidayList from "./HolidayList";

const HolidayListIndex = () => {
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
            { name: t("navigation.timeKeeping.title") },
            { name: t("navigation.timeSheet.categoryManagement") },
            { name: t("navigation.timeSheet.holidayList") },
          ]}
        />
      </div>
      <div>
        <Grid className="" container spacing={2}>
          <Grid item xs={12}>
            <ButtonGroup
              color="container"
              aria-label="outlined primary button group"
            >
              <Button startIcon={<AddIcon />} onClick={() => {}}>
                {!isMobile && "Thêm mới"}
              </Button>
              <Button startIcon={<SearchIcon />} onClick={() => {}}>
                {!isMobile && "Tìm kiếm"}
              </Button>
              <Button
                //   disabled={listSelected?.length === 0}
                startIcon={<DeleteOutlineIcon />}
              >
                {!isMobile && t("general.button.delete")}
              </Button>
              <Button startIcon={<CheckCircleOutlineIcon />} onClick={() => {}}>
                {!isMobile && "Duyệt"}
              </Button>
              <Button startIcon={<BlockIcon />} onClick={() => {}}>
                {!isMobile && "Chờ duyệt"}
              </Button>
              <Button startIcon={<HighlightOffIcon />} onClick={() => {}}>
                {!isMobile && "Từ chối"}
              </Button>
              <Button
                startIcon={<DashboardIcon />}
                aria-describedby={id}
                type="button"
                onClick={handleClick}
              >
                Khác
              </Button>
              <Popover
                id={id}
                open={open}
                anchorEl={anchorEl}
                onClose={handleClick}
                anchorOrigin={{
                  vertical: "bottom",
                  horizontal: "right",
                }}
                transformOrigin={{
                  vertical: "top",
                  horizontal: "right",
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
            <HolidayList />
          </Grid>
        </Grid>
      </div>
    </div>
  );
};

export default HolidayListIndex;
