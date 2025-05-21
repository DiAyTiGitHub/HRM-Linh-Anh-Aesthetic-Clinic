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
import DeleteOutlineIcon from "@material-ui/icons/DeleteOutline";
import PeriodSettingList from "./ScheduleManagerList";
import { Formik } from "formik";
import GlobitsAutocomplete from "app/common/form/GlobitsAutocomplete";
import EditIcon from '@material-ui/icons/Edit';

const options = [
  {
    id: 1,
    name: "Nhân viên 1",
  },
  {
    id: 2,
    name: "Nhân viên 2",
  },
];
const optionsWeek = [
  {
    id: 1,
    name: "Tuần 1",
  },
  {
    id: 2,
    name: "Tuần 2",
  },
];

const listDay = [
  { value: 1, name: "Thứ 2", date: "09/09/2024" },
  { value: 2, name: "Thứ 3", date: "10/09/2024" },
  { value: 3, name: "Thứ 4", date: "11/09/2024" },
  { value: 4, name: "Thứ 5", date: "12/09/2024" },
  { value: 5, name: "Thứ 6", date: "13/09/2024" },
  { value: 6, name: "Thứ 7", date: "14/09/2024" },
  { value: 7, name: "Chủ nhật", date: "15/09/2024" },
];

const ScheduleManagerIndex = () => {
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
            { name: t("navigation.timeSheet.workSchedule") },
            { name: t("navigation.timeSheet.scheduleManager") },
          ]}
        />
      </div>
      <div>
        <Grid className="" container spacing={2}>
          <Formik initialValues={{}} onSubmit={(values) => {}}>
            <>
              <Grid item sm={3} xs={3}>
                <GlobitsAutocomplete
                  name="staff"
                  label="Chọn nhân viên"
                  options={options}
                />
              </Grid>
              <Grid item sm={3} xs={3}>
                <GlobitsAutocomplete
                  name="week"
                  label="Chọn tuần"
                  options={optionsWeek}
                />
              </Grid>
              <Grid item sm={3} xs={3} className="flex align-end">
                <Button variant="outlined" className="mr-8" startIcon={<EditIcon />}>
                  Sửa
                </Button>
                <Button variant="outlined" startIcon={<DeleteOutlineIcon />}>
                  Xóa
                </Button>
              </Grid>
            </>
          </Formik>

          <Grid item xs={12}>
            <div className="table-root table-form">
              <table
                className="table-container"
                cellPadding={0}
                cellSpacing={0}
              >
                <thead>
                  <tr className="row-table-header">
                    <th align="left" width="10%">
                      STT
                    </th>
                    <th align="left">Thứ</th>
                    <th align="left">Ngày</th>
                    <th align="left">Ca sáng</th>
                    <th align="left">Ca chiều</th>
                    <th align="left">Ca đêm</th>
                  </tr>
                </thead>
                <tbody>
                  {listDay?.map((item, index) => (
                    <tr key={item.value}>
                      <td>{index}</td>
                      <td>{item.name}</td>
                      <td>{item.date}</td>
                      <td>
                        <input type="checkbox" />
                      </td>
                      <td>
                        <input type="checkbox" />
                      </td>
                      <td>
                        <input type="checkbox" />
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </Grid>
        </Grid>
      </div>
    </div>
  );
};

export default ScheduleManagerIndex;
