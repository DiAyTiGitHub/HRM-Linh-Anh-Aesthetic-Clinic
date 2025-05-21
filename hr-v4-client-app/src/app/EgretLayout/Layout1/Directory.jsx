import { Collapse, Grid, makeStyles } from "@material-ui/core";
import React from "react";
import CardMembershipIcon from "@material-ui/icons/CardMembership";
import ListAltIcon from "@material-ui/icons/ListAlt";
import PublicIcon from "@material-ui/icons/Public";
import StyleIcon from "@material-ui/icons/Style";
import StorageRoundedIcon from "@material-ui/icons/StorageRounded";
import FormatListBulletedIcon from "@material-ui/icons/FormatListBulleted";
import BusinessCenterIcon from "@material-ui/icons/BusinessCenter";
import QueryBuilderRoundedIcon from "@material-ui/icons/QueryBuilderRounded";
import InsertDriveFileSharpIcon from "@material-ui/icons/InsertDriveFileSharp";
import PersonAddSharpIcon from "@material-ui/icons/PersonAddSharp";
import DeviceHubIcon from "@material-ui/icons/DeviceHub";
import GradeSharpIcon from "@material-ui/icons/GradeSharp";
import HomeWorkSharpIcon from "@material-ui/icons/HomeWorkSharp";
import StarOutlineSharpIcon from "@material-ui/icons/StarOutlineSharp";
import AccountBoxIcon from "@material-ui/icons/AccountBox";
import GroupIcon from "@material-ui/icons/Group";
import SettingsIcon from "@material-ui/icons/Settings";
import PanoramaIcon from "@material-ui/icons/Panorama";
import SchoolIcon from "@material-ui/icons/School";
import SupervisedUserCircleIcon from "@material-ui/icons/SupervisedUserCircle";
import WorkSharpIcon from "@material-ui/icons/WorkSharp";
import AccountTreeSharpIcon from "@material-ui/icons/AccountTreeSharp";
import FlagSharpIcon from "@material-ui/icons/FlagSharp";
import BusinessIcon from '@material-ui/icons/Business';
import { NavLink } from "react-router-dom/cjs/react-router-dom.min";

const useStyles = makeStyles((theme) => ({
  filterPopup: {
    position: "absolute",
    top: "65px",
    background: "#ffffff",
    right: "0",
    width: "99.5% !important",
    boxShadow: "var(--elevation-z8)",
    // padding: "25px !important",
    border: "1px solid #a4c7e2",
    zIndex: "199999",
    "& .MuiCollapse-wrapper": {
      // padding: '8px'
    },
    "& hr": {
      marginTop: ".5rem",
      marginBottom: ".5rem",
      border: 0,
      borderTop: "1px solid rgba(0, 0, 0, 0.1)",
    },
  },
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
    padding: "1.5rem",
    maxHeight: "95vh",
    overflow: "auto",
    zIndex: "999 !important",

  },
}));

const IconAsset = () => (
  <svg width="24px" height="24px" viewBox="0 0 512 512" fill="#ffffff" stroke="#ffffff">
    <g strokeWidth="0"></g>
    <g strokeLinecap="round" strokeLinejoin="round"></g>
    <g><g>
      <polygon className="st0" fill="#ffffff" points="281.844,209.75 275.688,217.031 275.703,234.625 339.156,234.625 339.203,256.031 339.219,259.563 275.719,259.563 275.75,297.359 236.219,297.359 236.266,259.563 172.766,259.563 172.781,256.031 172.828,234.703 236.281,234.625 236.281,217.078 230.141,209.75 172.906,209.75 172.969,188.484 212.344,188.484 178.984,148.75 212.234,148.75 256,200.797 299.75,148.75 333,148.75 299.719,188.484 339.016,188.484 339.094,209.75 "></polygon>
      <path className="st0" fill="#ffffff" d="M256,55.109c-141.375,0-256,72.266-256,161.422v78.938c0,89.172,114.625,161.422,256,161.422 s256-72.25,256-161.422v-78.938C512,127.375,397.375,55.109,256,55.109z M256,89.734c120,0,221.375,58.063,221.375,126.797 C477.375,285.25,376,343.313,256,343.313S34.625,285.25,34.625,216.531C34.625,147.797,136,89.734,256,89.734z"></path>
    </g></g>
  </svg>
)

export default function Directory(props) {
  const classes = useStyles();

  const listLink1 = [
    {
      name: "Tình trạng nhân viên",
      path: "employee-status",
      className: "btn-link-purple",
      icon: <FormatListBulletedIcon />,
      title: "Quản lý danh sách tình trạng nhân viên",
    },
    {
      name: "Trạng thái làm việc",
      path: "working-status",
      className: "btn-link-yellow",
      icon: <BusinessCenterIcon />,
      title: "Quản lý danh sách trạng thái làm việc của nhân viên",
    },
    {
      name: "Hình thức khen thưởng",
      path: "reward",
      className: "btn-link-green",
      icon: <StyleIcon />,
      title: "Quản lý các hình thức khen thưởng dành cho nhân viên",
    },
    {
      name: "Ca làm việc",
      path: "shift-work",
      className: "btn-link-pink",
      icon: <QueryBuilderRoundedIcon />,
      title: "Quản lý ca, kíp làm việc",
    },
    {
      name: "Loại hợp đồng",
      path: "contracttype",
      className: "btn-link-blue",
      icon: <InsertDriveFileSharpIcon />,
      title: "Quản lý các loại hợp đồng",
    },
    // {
    //   name: "Loại phụ cấp",
    //   path: "allowanceType",
    //   className: "btn-link-darkolivegreen",
    //   icon: <StorageRoundedIcon />,
    //   title: "Quản lý các loại phụ cấp",
    // },
    {
      name: "Quan hệ thân nhân",
      path: "familyRelationship",
      className: "btn-link-black",
      icon: <PersonAddSharpIcon />,
      title: "Quản lý danh mục quan hệ thân nhân của nhân viên",
    },
    {
      name: "Công cụ/ dụng cụ",
      path: "asset-management/product",
      className: "btn-link-green",
      icon: <IconAsset />,
      title: "Quản lý công cụ/ dụng cụ",
    },
  ];

  const listLink2 = [
    {
      name: "Chứng chỉ",
      path: "certificate",
      className: "btn-link-purple",
      icon: <CardMembershipIcon />,
      title: "Quản lý các loại chứng chỉ",
    },
    {
      name: "Công chức",
      path: "duty/grade",
      className: "btn-link-yellow",
      icon: <DeviceHubIcon />,
      title:
        "Quản lý các danh mục công chức, bậc công chức, mã ngạch và phân loại",
    },
    {
      name: "Trình độ",
      path: "degree/professional",
      className: "btn-link-green",
      icon: <GradeSharpIcon />,
      title:
        "Quản lý danh mục trình đọ như: trình đọ chuyển môn, lý luận chính chỉ,...",
    },
    {
      name: "Cơ sở đào tạo",
      path: "educationalInstitution",
      className: "btn-link-pink",
      icon: <HomeWorkSharpIcon />,
      title: "Quản lý cơ sở đạo tạo",
    },
    {
      name: "Học Hàm",
      path: "academic",
      className: "btn-link-blue",
      icon: <SchoolIcon />,
      title: "Quản lý danh mục học hàm",
    },
    {
      name: "Chuyên ngành",
      path: "speciality",
      className: "btn-link-darkolivegreen",
      icon: <StarOutlineSharpIcon />,
      title: "Quản lý danh mục các chuyên ngành",
    },
    {
      name: "Danh hiệu",
      path: "titleConferred",
      className: "btn-link-black",
      icon: <AccountBoxIcon />,
      title: "Quan lý các danh hiệu được phong",
    },
  ];

  const listLink3 = [
    {
      name: "Đơn vị",
      path: "organization",
      className: "btn-link-black",
      icon: <BusinessIcon />,
      title: "Quản lý các thông tin của các công ty, tổ chức",
      auth: ["ROLE_ADMIN"],
    },
    {
      name: "Đơn vị hành chính",
      path: "administrative-unit",
      className: "btn-link-purple",
      icon: <ListAltIcon />,
      title: "Quản lý danh mục đơn vị hành chính Việt Nam",
    },
    // {
    //   name: "Phòng ban",
    //   path: "department",
    //   className: "btn-link-yellow",
    //   icon: <AccountTreeSharpIcon />,
    //   title: "Quản lý các phong ban của tổ chức",
    // },
    {
      name: "Quốc gia",
      path: "country",
      className: "btn-link-green",
      icon: <PublicIcon />,
      title: "Quản lý danh mục quốc gia",
    },
    {
      name: "Dân tộc",
      path: "ethnics",
      className: "btn-link-pink",
      icon: <SupervisedUserCircleIcon />,
      title: "Quản lý danh mục dân tộc Việt Nam",
    },
    {
      name: "Tôn giáo",
      path: "religion",
      className: "btn-link-blue",
      icon: <PublicIcon />,
      title: "Quản lý danh mục các tôn giáo",
    },
    {
      name: "Nghề nghiệp",
      path: "profession",
      className: "btn-link-darkolivegreen",
      icon: <WorkSharpIcon />,
      title: "Quản lý danh mục nghề nghiệp",
    },
    {
      name: "Kỷ luật",
      path: "discipline",
      className: "btn-link-black",
      icon: <FlagSharpIcon />,
      title: "Quản lý danh mục kỷ luật",
    },

  ];

  function renderNavlink(listLink) {
    return listLink.map((item, index) => (
      <React.Fragment key={index}>
        <NavLink to={`/category/${item.path}`}
          className={`directory-btn-link ${item.className}`}
          onClick={() => props.setState()}
        >
          {item.icon ? item.icon : <></>}
          <div>
            <p>{item.name}</p>
            <p>{item?.title}</p>
          </div>
        </NavLink>
        <hr />
      </React.Fragment>
    ));
  }

  return (
    <Collapse in={props.checked} className={classes.filterPopup}>
      <Grid container spacing={4} className={classes.content}>
        <Grid item xs={12} md={4} className={classes.gridFlex}>
          <h5>
            <GroupIcon />
            Nhân viên
          </h5>
          <div>{renderNavlink(listLink1)}</div>
        </Grid>
        <Grid item xs={12} md={4} className={classes.gridFlex}>
          <h5>
            <PanoramaIcon />
            Bằng cấp & Đạo tạo
          </h5>
          <div>{renderNavlink(listLink2)}</div>
        </Grid>
        <Grid item xs={12} md={4} className={classes.gridFlex}>
          <h5>
            <SettingsIcon />
            Danh mục khác
          </h5>
          <div>{renderNavlink(listLink3)}</div>
        </Grid>
      </Grid>
    </Collapse>
  );
}
