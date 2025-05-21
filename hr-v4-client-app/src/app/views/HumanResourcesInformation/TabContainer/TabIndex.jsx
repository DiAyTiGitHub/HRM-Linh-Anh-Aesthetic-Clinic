import { Box, Grid, Typography } from "@material-ui/core";
import { makeStyles } from "@material-ui/core/styles";
import { AccountTree, DnsOutlined, Policy } from "@material-ui/icons";
import AccountBalanceIcon from "@material-ui/icons/AccountBalance";
import AccountBalanceWalletIcon from "@material-ui/icons/AccountBalanceWallet";
import AssignmentIcon from "@material-ui/icons/Assignment";
import AssignmentIndIcon from "@material-ui/icons/AssignmentInd";
import AssignmentTurnedInIcon from "@material-ui/icons/AssignmentTurnedIn";
import AttachMoneyIcon from "@material-ui/icons/AttachMoney";
import CastForEducationIcon from "@material-ui/icons/CastForEducation";
import CreditCardIcon from "@material-ui/icons/CreditCard";
import EditIcon from "@material-ui/icons/Edit";
import FeaturedPlayListIcon from "@material-ui/icons/FeaturedPlayList";
import FlightTakeoffIcon from "@material-ui/icons/FlightTakeoff";
import FolderSharedIcon from "@material-ui/icons/FolderShared";
import HowToRegIcon from "@material-ui/icons/HowToReg";
import LocalHospitalIcon from "@material-ui/icons/LocalHospital";
import MonetizationOnIcon from "@material-ui/icons/MonetizationOn";
import PieChartOutlinedIcon from "@material-ui/icons/PieChartOutlined";
import PinDropIcon from "@material-ui/icons/PinDrop";
import PregnantWomanIcon from "@material-ui/icons/PregnantWoman";
import WorkOffIcon from "@material-ui/icons/WorkOff";
import TabsComponent from "app/common/Tab/TabComponent";
import StaffLeaveIndex from "app/views/HumanResourcesInformation/TabContainer/StaffLeave/StaffLeaveIndex";
import LeaveRequestTabIndex from "app/views/LeaveRequest/LeaveRequestTabIndex";
import { useFormikContext } from "formik";
import { observer } from "mobx-react";
import PropTypes from "prop-types";
import React, { memo, useEffect } from "react";
import { useTranslation } from "react-i18next";
import { useParams } from "react-router-dom/cjs/react-router-dom.min";
import "../HRInformation.scss";
import Asset from "./Asset";
import GeneralInformation from "./GeneralInformation";
import OtherIncomeTab from "./OtherIncomeTab/OtherIncomeTab";
import StaffAgreementsV2 from "./StaffAgreementsV2";
import StaffApplyProcess from "./StaffApplyProcess";
import StaffDisciplineHistoryIndex from "./StaffDisciplineHistory/StaffDisciplineHistoryIndex";
import StaffDocumentItemsV2 from "./StaffDocumentItemsV2";
import StaffEducationHistoryV2 from "./StaffEducationHistoryV2";
import StaffIntroduceCosts from "./StaffIntroduceCosts";
import StaffMaternityHistoryV2 from "./StaffMaternityHistoryV2";
import StaffPersonBankAccounts from "./StaffPersonBankAccounts";
import StaffPositionsV2 from "./StaffPositionsV2";
import StaffRewardHistoryIndex from "./StaffRewardHistory/StaffRewardHistoryIndex";
import StaffSalaryItemValueIndex from "./StaffSalaryItemValue/StaffSalaryItemValueIndex";
import StaffSalaryTemplate from "./StaffSalaryTemplate";
import StaffShiftWorkAssignment from "./StaffShiftWorkAssignment";
import StaffSignatureV2 from "./StaffSignatureV2";
import StaffWorkingLocations from "./StaffWorkingLocations";
import TabProfileDiagram from "./TabProfileDiagram";
import BusinessCenterIcon from '@material-ui/icons/BusinessCenter';
import StaffInsurancePackage from "app/views/HumanResourcesInformation/TabContainer/StaffInsurancePackage";
import StafffInsuranceProcessWrapper from "./StafffInsuranceProcessWrapper";

const useStyles = makeStyles((theme) => ({
  root: {
    flexGrow: 1,
    width: "100%",
    backgroundColor: theme.palette.background.paper,

    "& .MuiBox-root": {
      padding: "10px !important",
    },

    "& .MuiTabPanel-root": {
      transition: "all .2s ease",
    },
  },
  tabItem: {
    padding: "1.2px 8px !important",
    margin: "unset !important",
    minWidth: "unset !important",
    minHeight: "unset !important",

    display: "flex",

    "& .MuiTab-wrapper": {
      flexDirection: "row",
      padding: "12px",

      "& svg": {
        margin: "unset !important",
      },
    },
  },
  indicator: {
    backgroundColor: "#2f4f4f !important",
  },
}));

function StaffProfileTabIndex(props) {
  const { t } = useTranslation();
  const classes = useStyles();
  const { values } = useFormikContext();
  const { id } = useParams();
  const { value, handleChangeValue } = props;

  /// allowedInCreateMode: hiển thị khi tạo mới (mặc định là false)
  /// showActionButtons: để kiểm soát việc hiển thị nút Save và Cancel
  const tabsList = [
    // Tab 0 - Thông tin cá nhân
    {
      icon: <AssignmentIndIcon />,
      label: t("humanResourcesInformation.personalInformation"),
      content: <GeneralInformation />,
      allowedInCreateMode: true,
      showActionButtons: true,
    },

    // Tab 1: Sơ đồ tổ chức của cá nhân
    {
      icon: <AccountTree />,
      label: "Sơ đồ tổ chức",
      content: <TabProfileDiagram />,
    },

    // Tab 2 - Vị trí công tác
    {
      icon: <DnsOutlined />,
      label: t("humanResourcesInformation.positionStaff"),
      // content: <StaffPosition />,
      content: <StaffPositionsV2 />,
    },

    // Tab 3: Các địa điểm làm việc của nhân viên
    {
      icon: <PinDropIcon />,
      label: "Các địa điểm làm việc của nhân viên",
      content: <StaffWorkingLocations />,
    },

    // Tab  các vị trí của nhân viên
    // {
    //   icon: <DnsOutlined />,
    //   label: t("humanResourcesInformation.positionStaff"),
    //   content: <PositionStaff />,
    // },
    // // Tab 2 phụ cấp nhân viên
    // {
    //     icon: <ChromeReaderModeIcon />,
    //     label: t("humanResourcesInformation.allowance"),
    //     content: <StaffAllowance />,
    // },

    // Tab  - Account Information (Commented out)
    // {
    //   icon: <InfoIcon fontSize="small" />,
    //   label: t("humanResourcesInformation.accountInformation"),
    //   content: <AccountInformation />,
    // },

    // Tab 17: Tài khoản ngân hàng của nhân viên
    {
      icon: <CreditCardIcon />,
      label: "Tài khoản ngân hàng",
      content: <StaffPersonBankAccounts />,
    },

    // Tab 4 - Assets:công cụ/ dụng cụ
    {
      icon: <AccountBalanceIcon />,
      label: "Quản lý công cụ, dụng cụ",
      content: <Asset />,
    },

    {
      icon: <BusinessCenterIcon />,
      label: "Gói bảo hiểm",
      content: <StaffInsurancePackage />,
    },

    // Tab 5 - Education History: Quá trình đào tạo
    {
      icon: <CastForEducationIcon />,
      label: t("humanResourcesInformation.educationHistory"),
      content: <StaffEducationHistoryV2 />,
    },

    // Tab 6 - Agreements: hợp đồng
    {
      icon: <AssignmentIcon />,
      label: t("humanResourcesInformation.agreements"),
      content: <StaffAgreementsV2 />,
    },
    // Lương phụ cấp
    {
      icon: <AttachMoneyIcon />,
      label: t("Lương phụ cấp"),
      content: <StaffSalaryItemValueIndex />,
    },
    // Tab 7 - Mẫu bảng lương áp dụng cho nhân viên
    {
      icon: <FeaturedPlayListIcon />,
      label: t("humanResourcesInformation.staffSalaryTemplate"),
      content: <StaffSalaryTemplate />,
    },
    {
      icon: <MonetizationOnIcon />,
      label: t("Thu nhập/ khấu trừ khác"),
      content: <OtherIncomeTab />,
    },
    // Tab 8 - Insurance History: Quá trình đóng BHXH
    {
      icon: <LocalHospitalIcon />,
      label: t("humanResourcesInformation.insuranceHistory"),
      content: <StafffInsuranceProcessWrapper />,
    },
    // Tab 9 - Maternity History (Visible only for Female users): Quá trình thai sản
    {
      icon: <PregnantWomanIcon />,
      label: t("humanResourcesInformation.maternityHistory"),
      content: values?.gender === "F" ? <StaffMaternityHistoryV2 /> : "",
    },

    // Tab 10 - Ca làm việc được phân
    {
      icon: <PieChartOutlinedIcon />,
      label: "Ca làm việc được phân",
      content: <StaffShiftWorkAssignment />,
    },

    // Tab 11 - Quá trình ứng tuyển
    {
      icon: <FolderSharedIcon />,
      label: "Quá trình ứng tuyển",
      content: <StaffApplyProcess />,
    },

    // Tab 12: Chữ ký nhân viên
    {
      icon: <EditIcon />,
      label: "Chữ ký nhân viên",
      content: <StaffSignatureV2 />,
    },

    // Tab 13: Chi phí giới thiệu
    {
      icon: <HowToRegIcon />,
      label: "Chi phí giới thiệu",
      content: <StaffIntroduceCosts />,
    },

    // Tab 14: các tài liệu nhân viên đã nộp
    {
      icon: <AssignmentTurnedInIcon />,
      label: "Các tài liệu nhân viên đã nộp",
      content: <StaffDocumentItemsV2 />,
    },
    // Tab 15: khen thưởng, kỷ luật
    {
      icon: <AccountBalanceWalletIcon />,
      label: t("humanResourcesInformation.rewardHistory"),
      content: <StaffRewardHistoryIndex />,
    },
    // Tab 16: quá trình kỷ luật
    {
      icon: <Policy />,
      label: t("disciplineHistory.title"),
      content: <StaffDisciplineHistoryIndex />,
    },
    // Tab 17: Nghỉ việc
    {
      icon: <FlightTakeoffIcon />,
      label: t("Nghỉ phép"),
      content: <LeaveRequestTabIndex />,
    },
    {
      icon: <WorkOffIcon />,
      label: t("staffLeave.title"),
      content: <StaffLeaveIndex />,
    },
  ];

  const filterTabs = (tabs) => {
    if (!id) {
      // Chế độ tạo mới
      return tabs.filter(
        (tab) =>
          tab.allowedInCreateMode ||
          (values?.gender === "F" && tab.label === t("humanResourcesInformation.maternityHistory"))
      );
    } else {
      // Chế độ chỉnh sửa/xem
      return values?.gender === "F"
        ? tabs
        : tabs.filter((tab) => tab.label !== t("humanResourcesInformation.maternityHistory"));
    }
  };

  const filteredTabsList = filterTabs(tabsList);

  const handleChange = (event, newValue) => {
    let title = filteredTabsList[newValue].label;
    handleChangeValue(event, newValue, title, filteredTabsList[newValue]);
  };

  useEffect(() => {
    let title = filteredTabsList[0].label;
    handleChangeValue(null, 0, title, filteredTabsList[0]);
  }, []);

  return (
    <div className={`${classes.root} pb-48 index-card`} value={value} index={0}>
      <Grid item xs={12}>
        <TabsComponent
          value={value}
          handleChange={handleChange}
          tabList={filteredTabsList}
          displayMode='icon-only'
        />
      </Grid>
      <Grid item xs={12} className='p-0'>
        {filteredTabsList?.map(function (tab, index) {
          return (
            <TabPanel key={index} value={value} index={index}>
              {tab?.content}
            </TabPanel>
          );
        })}
      </Grid>
    </div>
  );
}

export default memo(observer(StaffProfileTabIndex));

function TabPanel(props) {
  const { children, value, index, ...other } = props;
  return (
    <React.Fragment>
      <div
        role='tabpanel'
        hidden={value !== index}
        id={`scrollable-force-tabpanel-${index}`}
        aria-labelledby={`scrollable-force-tab-${index}`}
        {...other}>
        {value === index && (
          <Box p={3}>
            <Typography>{children}</Typography>
          </Box>
        )}
      </div>
    </React.Fragment>
  );
}

TabPanel.propTypes = {
  children: PropTypes.node,
  index: PropTypes.any.isRequired,
  value: PropTypes.any.isRequired,
};
