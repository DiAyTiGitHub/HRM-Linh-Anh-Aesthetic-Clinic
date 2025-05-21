import { Button, ButtonGroup, Collapse, Grid, makeStyles, TextField } from "@material-ui/core";
import HighlightOffIcon from "@material-ui/icons/HighlightOff";
import RotateLeftIcon from "@material-ui/icons/RotateLeft";
import SearchIcon from "@material-ui/icons/Search";
import { Autocomplete } from "@material-ui/lab";
import TreeAutocompleteSelector from "app/common/SelectComponent/SelectDepartmentTreeView/TreeAutocompleteSelector";
import GlobitsCheckBox from "app/common/form/GlobitsCheckBox";
import GlobitsPagingAutocompleteV2 from "app/common/form/GlobitsPagingAutocompleteV2";
import GlobitsSelectInput from "app/common/form/GlobitsSelectInput";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import { useStore } from "app/stores";
import { pagingDepartmentHierarchy } from "app/views/Department/DepartmentService";
import { pagingAllOrg } from "app/views/Organization/OrganizationService";
import { pagingPosition } from "app/views/Position/PositionService";
import { pagingWorkplace } from "app/views/Workplace/WorkplaceService";
import { useFormikContext } from "formik";
import { observer } from "mobx-react";
import { memo, useEffect } from "react";
import { useTranslation } from "react-i18next";
import LocalConstants, {
  LIST_MONTH,
  ListGender,
  ListMaritalStatus,
  StaffDocumentStatus,
  StaffPhase
} from "../../LocalConstants";
import GlobitsDateTimePicker from "../../common/form/GlobitsDateTimePicker";
import GlobitsNumberInput from "../../common/form/GlobitsNumberInput";
import { pagingAdministratives } from "../AdministrativeUnit/AdministrativeUnitService";
import { pagingEmployeeStatus } from "../EmployeeStatus/EmployeeStatusService";
import { pagingPositionTitle } from "../PositionTitle/PositionTitleService";
import { pagingRankTitle } from "../RankTitle/RankTitleService";
import { pagingStaff } from "./StaffService";
import { pagingShiftWork } from "../ShiftWork/ShiftWorkService";

const useStyles = makeStyles ((theme) => ({
  autoHeight:{
    "& .MuiAutocomplete-root":{
      height:"auto", // Tự động điều chỉnh chiều cao
    },
    "& .MuiAutocomplete-inputRoot":{
      flexWrap:"wrap", // Cho phép các chip xuống dòng
      alignItems:"flex-start", // Căn các chip lên trên
      paddingTop:"5px", // Thêm padding để chip không bị cắt
      paddingBottom:"5px",
      height:"auto",
    },
    "& .MuiAutocomplete-input":{},
    "& .MuiChip-root":{
      margin:"2px", // Khoảng cách giữa các chip
    },
  },
}));

function StaffFilter (props) {
  const classes = useStyles ();
  const {staffStore} = useStore ();
  const {values, setFieldValue, resetForm} = useFormikContext ();

  const {t} = useTranslation ();
  const {
    isOpenFilter,
    handleCloseFilter,
    setCountFilterActive
  } = props;

  async function handleResetFilter () {
    await staffStore.resetSearchStaff ();
    resetForm ();
  }

  function countActiveFiltersFromSearchObject (values) {
    // các trường cần đếm
    const includeFields = [
      "contractOrganization",
      "workOrganization",
      "employeeStatus",
      "fromStartDate",
      "toStartDate",
      "fromBirthDate",
      "toBirthDate",
      "workingLocation",
      "staffPhase",
      "contractNumber",
      "gender",
      "province",
      "district",
      "administrativeunit",
      "currentResidence",
      "birthPlace",
      "idNumber",
      "maritalStatus",
      "taxCode",
      "healthInsuranceNumber",
      "socialInsuranceNumber",
      "socialInsuranceNote",
      "hasSocialIns",
      "introducer",
      "recruiter",
      "staffDocumentStatus",
      "organization",
      "department",
      "positionTitle",
      "rankTitle",
      "position",
      "directManager",
    ];

    let count = 0;

    Object.entries (values).forEach (([key, value]) => {
      if (includeFields.includes (key)) {
        if (value && typeof value === "object") {
          if ("id" in value || Object.keys (value).length > 0) {
            count++;
          }
        } else if (
            value === true ||
            (value !== null && value !== undefined && value !== "" && value !== false)
        ) {
          count++;
        }
      }
    });

    setCountFilterActive (count);
  }

  useEffect (() => {
    countActiveFiltersFromSearchObject (values);
  }, [values]);

  return (
      <Collapse in={isOpenFilter} className='filterPopup'>
        <div className='flex flex-column'>
          <Grid container spacing={2}>
            <Grid item xs={12}>
              <div className='filterContent pt-8'>
                <Grid container spacing={2}
                    // className={"flex flex-end"}
                >
                  <Grid item xs={12} className='pb-0'>
                    <p className='m-0 p-0 borderThrough2'>Tiêu chí</p>
                  </Grid>

                  <Grid item xs={12} sm={6} md={4} lg={3}>
                    <GlobitsPagingAutocompleteV2
                        name='contractOrganization'
                        label='Đơn vị kí hợp đồng'
                        api={pagingAllOrg}
                    />
                  </Grid>

                  <Grid item xs={12} sm={6} md={4} lg={3}>
                    <GlobitsPagingAutocompleteV2
                        name='workOrganization'
                        label='Đơn vị làm việc'
                        api={pagingAllOrg}
                    />
                  </Grid>
                  <Grid item xs={12} sm={6} md={4} lg={3}>
                    <GlobitsPagingAutocompleteV2
                        name='employeeStatus'
                        api={pagingEmployeeStatus}
                        label='Trạng thái nhân viên'
                        handleChange={(_, employeeStatus) => {
                          setFieldValue ("employeeStatus", employeeStatus);
                          setFieldValue ("employeeStatusId", employeeStatus?.id);
                        }}
                    />
                  </Grid>
                  <Grid item xs={12} sm={6} md={4} lg={3}>
                    <GlobitsDateTimePicker label='Tuyển dụng từ ngày' name='fromRecruitmentDate'/>
                  </Grid>

                  <Grid item xs={12} sm={6} md={4} lg={3}>
                    <GlobitsDateTimePicker label='Tuyển dụng đến ngày' name='toRecruitmentDate'/>
                  </Grid>

                  <Grid item xs={12} sm={6} md={4} lg={3}>
                    <label className='label-container'>Sinh nhật trong tháng</label>
                    <Autocomplete
                        className={classes.autoHeight}
                        multiple
                        options={LIST_MONTH}
                        getOptionLabel={(option) => option.name}
                        value={LIST_MONTH.filter ((m) => values.birthMonths?.includes (m.value))}
                        onChange={(event, newValue) => {
                          const birthMonths = newValue?.map ((item) => item.value);
                          setFieldValue ("birthMonths", birthMonths);
                        }}
                        renderInput={(params) => (
                            <TextField
                                {... params}
                                variant='outlined'
                                InputProps={{
                                  ... params.InputProps,
                                  style:{
                                    flexWrap:"wrap", // Quan trọng: cho phép xuống dòng
                                    alignItems:"flex-start",
                                  },
                                }}
                                inputProps={{
                                  ... params.inputProps,
                                  style:{
                                    minWidth:"50px", // Đảm bảo input có độ rộng tối thiểu
                                  },
                                }}
                            />
                        )}
                        getOptionSelected={(option, value) => option.value === value.value}
                        ChipProps={{
                          size:"small", // Chip nhỏ gọn hơn
                        }}
                    />
                  </Grid>
                  <Grid item xs={12} sm={6} md={4} lg={3}>
                    <GlobitsDateTimePicker label='Ngày sinh từ' name='fromBirthDate'/>
                  </Grid>

                  <Grid item xs={12} sm={6} md={4} lg={3}>
                    <GlobitsDateTimePicker label='Ngày sinh đến' name='toBirthDate'/>
                  </Grid>
                  <Grid item xs={12} sm={6} md={4} lg={3}>
                    <GlobitsPagingAutocompleteV2
                        name='workplace'
                        label='Địa điểm làm việc'
                        api={pagingWorkplace}
                        handleChange={(_, workplace) => {
                          setFieldValue ("workplace", workplace);
                          setFieldValue ("workplaceId", workplace?.id);
                        }}
                    />
                  </Grid>
                  <Grid item xs={12} sm={6} md={4} lg={3}>
                    <GlobitsSelectInput
                        label={"Tình trạng nhân viên"}
                        name='staffPhase'
                        keyValue='value'
                        options={StaffPhase.getListData ()}
                    />
                  </Grid>
                  <Grid item xs={12} sm={6} md={4} lg={3}>
                    <GlobitsTextField label={"Số hợp đồng"} name='contractNumber'/>
                  </Grid>

                  <Grid item xs={12} sm={6} md={4} lg={3}>
                    <GlobitsSelectInput
                        label={"Giới tính"}
                        name='gender'
                        keyValue='id'
                        options={ListGender}
                    />
                  </Grid>
                  <Grid item xs={12} sm={6} md={4} lg={3}>
                    <GlobitsPagingAutocompleteV2
                        label={t ("Tỉnh thường trú")}
                        name='province'
                        value={values?.province}
                        api={pagingAdministratives}
                        searchObject={{level:3}}
                        handleChange={(_, value) => {
                          setFieldValue ("province", value);
                          setFieldValue ("provinceId", value?.id? value?.id : null);

                          setFieldValue ("district", null);
                          setFieldValue ("districtId", null);
                          setFieldValue ("commune", null);
                          setFieldValue ("communeId", null);
                        }}
                    />
                  </Grid>

                  <Grid item xs={12} sm={6} md={4} lg={3}>
                    <GlobitsPagingAutocompleteV2
                        label={t ("Huyện thường trú")}
                        name='district'
                        value={values?.district}
                        api={pagingAdministratives}
                        searchObject={{
                          level:2,
                          provinceId:values?.province?.id,
                        }}
                        allowLoadOptions={!!values?.province?.id}
                        clearOptionOnClose
                        handleChange={(_, value) => {
                          setFieldValue ("district", value);
                          setFieldValue ("districtId", value?.id? value?.id : null);

                          setFieldValue ("commune", null);
                          setFieldValue ("communeId", null);
                        }}
                    />
                  </Grid>

                  <Grid item xs={12} sm={6} md={4} lg={3}>
                    <GlobitsPagingAutocompleteV2
                        label={t ("Xã thường trú")}
                        name='administrativeunit'
                        api={pagingAdministratives}
                        searchObject={{
                          level:1,
                          districtId:values?.district?.id,
                        }}
                        allowLoadOptions={!!values?.district?.id}
                        clearOptionOnClose
                        handleChange={(_, value) => {
                          setFieldValue ("commune", value);
                          setFieldValue ("communeId", value?.id? value?.id : null);
                        }}
                    />
                  </Grid>
                  <Grid item xs={12} sm={6} md={4} lg={3}>
                    <GlobitsTextField label={"Tạm trú"} name='currentResidence'/>
                  </Grid>
                  <Grid item xs={12} sm={6} md={4} lg={3}>
                    <GlobitsTextField label={"Quê quán"} name='birthPlace'/>
                  </Grid>
                  <Grid item xs={12} sm={6} md={4} lg={3}>
                    <GlobitsNumberInput label={"CMND/ CCCD"} name='idNumber'/>
                  </Grid>
                  <Grid item xs={12} sm={6} md={4} lg={3}>
                    <GlobitsSelectInput
                        label={"Tình trạng hôn nhân"}
                        name='maritalStatus'
                        keyValue='value'
                        options={ListMaritalStatus}
                    />
                  </Grid>
                  <Grid item xs={12} sm={6} md={4} lg={3}>
                    <GlobitsTextField label={"Mã số thuế"} name='taxCode'/>
                  </Grid>
                  <Grid item xs={12} sm={6} md={4} lg={3}>
                    <GlobitsTextField label={"Mã số BHYT"} name='healthInsuranceNumber'/>
                  </Grid>
                  <Grid item xs={12} sm={6} md={4} lg={3}>
                    <GlobitsTextField label={"Mã số BHXH"} name='socialInsuranceNumber'/>
                  </Grid>
                  <Grid item xs={12} sm={6} md={4} lg={3}>
                    <GlobitsTextField label={"Tình trạng sổ BHXH"} name='socialInsuranceNote'/>
                  </Grid>
                  <Grid item xs={12} sm={6} md={4} lg={3} className='pt-28 pl-20'>
                    <GlobitsCheckBox label='Có tham gia BHXH' name='hasSocialIns'/>
                  </Grid>
                  <Grid item xs={12} sm={6} md={4} lg={3}>
                    <GlobitsPagingAutocompleteV2
                        name='introducer'
                        api={pagingStaff}
                        label='Người giới thiệu'
                        handleChange={(_, value) => {
                          setFieldValue ("introducer", value);
                          setFieldValue ("introducerId", value?.id);
                        }}
                        getOptionLabel={(option) => {
                          if (!option) return "";

                          const name = option.displayName || "";
                          const code = option.staffCode? ` - ${option.staffCode}` : "";
                          const position = option.currentPosition?.name
                              ? ` (${option.currentPosition.name})`
                              : "";

                          return `${name}${code}${position}`;
                        }}
                    />
                  </Grid>
                  <Grid item xs={12} sm={6} md={4} lg={3}>
                    <GlobitsPagingAutocompleteV2
                        name='recruiter'
                        api={pagingStaff}
                        label='Người quyết định tuyển dụng'
                        handleChange={(_, value) => {
                          setFieldValue ("recruiter", value);
                          setFieldValue ("recruiterId", value?.id);
                        }}
                        getOptionLabel={(option) => {
                          if (!option) return "";

                          const name = option.displayName || "";
                          const code = option.staffCode? ` - ${option.staffCode}` : "";
                          const position = option.currentPosition?.name
                              ? ` (${option.currentPosition.name})`
                              : "";

                          return `${name}${code}${position}`;
                        }}
                    />
                  </Grid>
                  <Grid item xs={12} sm={6} md={4} lg={3}>
                    <GlobitsSelectInput
                        label={"Tình trạng nộp hồ sơ"}
                        name='staffDocumentStatus'
                        keyValue='value'
                        options={StaffDocumentStatus.getListData ()}
                    />
                  </Grid>
                  <Grid item xs={12} sm={6} md={4} lg={3}>
                    <GlobitsDateTimePicker label='Chính thức từ ngày' name='fromStartDate'/>
                  </Grid>

                  <Grid item xs={12} sm={6} md={4} lg={3}>
                    <GlobitsDateTimePicker label='Chính thức đến ngày' name='toStartDate'/>
                  </Grid>

                  <Grid item xs={12} sm={6} md={4} lg={3}>
                    <GlobitsDateTimePicker label='Nghỉ thai sản từ ngày' name='fromMaternityLeave'/>
                  </Grid>

                  <Grid item xs={12} sm={6} md={4} lg={3}>
                    <GlobitsDateTimePicker label='Nghỉ thai sản đến ngày' name='toMaternityLeave'/>
                  </Grid>
                  <Grid item xs={12} sm={6} md={4} lg={3} className='pt-28 pl-20'>
                    <GlobitsCheckBox label='Đã hết thời gian nghỉ chế độ thai sản' name='maternityLeaveEnded'/>
                  </Grid>
                  <Grid item xs={12} className='pb-0'>
                    <p className='m-0 p-0 borderThrough2'>Lịch làm việc</p>
                  </Grid>

                  <Grid item xs={12} sm={6} md={4} lg={3}>
                    <GlobitsSelectInput
                        label={"Loại phân ca"}
                        name='staffWorkShiftType'
                        keyValue='value'
                        hideNullOption
                        options={LocalConstants.StaffWorkShiftType.getListData ()}
                    />
                  </Grid>


                  {values?.staffWorkShiftType == LocalConstants.StaffWorkShiftType.FIXED.value && (
                      <Grid item xs={12} sm={6} md={4} lg={3}>
                        <GlobitsPagingAutocompleteV2
                            name='fixShiftWork'
                            label={"Ca làm việc cố định"}
                            api={pagingShiftWork}
                            getOptionLabel={(option) =>
                                option?.name && option?.code
                                    ? `${option.name} - ${option.code}`
                                    : option?.name || option?.code || ""
                            }
                        />
                      </Grid>

                  )}

                  <Grid item xs={12} sm={6} md={4} lg={3}>
                    <GlobitsSelectInput
                        label={"Loại nghỉ trong tháng"}
                        name='staffLeaveShiftType'
                        keyValue='value'
                        hideNullOption
                        options={LocalConstants.StaffLeaveShiftType.getListData ()}
                    />
                  </Grid>

                  {values?.staffLeaveShiftType == LocalConstants.StaffLeaveShiftType.FIXED.value && (
                      <Grid item xs={12} sm={6} md={4} lg={3}>
                        <GlobitsSelectInput
                            label={"Ngày nghỉ cố định"}
                            name='fixLeaveWeekDay'
                            keyValue='value'
                            hideNullOption
                            options={LocalConstants.WeekDays.getListData ()}
                        />
                      </Grid>
                  )}

                  <Grid item xs={12} className='pb-0'>
                    <p className='m-0 p-0 borderThrough2'>Vị trí công tác chính</p>
                  </Grid>

                  <Grid item xs={12} sm={6} md={4} lg={3}>
                    <GlobitsPagingAutocompleteV2
                        name='rankTitle'
                        label='Cấp bậc'
                        api={pagingRankTitle}
                        handleChange={(_, value) => {
                          setFieldValue ("rankTitle", value);
                          setFieldValue ("rankTitleId", value?.id);
                        }}
                    />
                  </Grid>
                  <Grid item xs={12} sm={6} md={4} lg={3}>
                    <GlobitsPagingAutocompleteV2
                        name='position'
                        label='Vị trí làm việc'
                        api={pagingPosition}
                        handleChange={(_, value) => {
                          setFieldValue ("position", value);
                          setFieldValue ("positionId", value?.id);
                        }}
                        getOptionLabel={(option) => {
                          if (!option) return "";

                          const name = option.name || "";
                          const staffName = option?.staff?.displayName
                              ? ` - ${option?.staff?.displayName}`
                              : " - vacant";
                          const departmentName = option?.department?.name
                              ? ` - (${option?.department?.name})`
                              : "";

                          return `${name}${staffName}${departmentName}`;
                        }}
                    />
                  </Grid>
                  <Grid item xs={12} sm={6} md={4} lg={3}>
                    <GlobitsPagingAutocompleteV2
                        name='directManager'
                        api={pagingStaff}
                        label='Người quản lý trực tiếp'
                        handleChange={(_, value) => {
                          setFieldValue ("directManager", value);
                          setFieldValue ("directManagerId", value?.id);
                        }}
                        getOptionLabel={(option) => {
                          if (!option) return "";

                          const name = option.displayName || "";
                          const code = option.staffCode? ` - ${option.staffCode}` : "";
                          const position = option.currentPosition?.name
                              ? ` (${option.currentPosition.name})`
                              : "";

                          return `${name}${code}${position}`;
                        }}
                    />
                  </Grid>

                  <Grid item xs={12} sm={6} md={4} lg={3}>
                    <GlobitsPagingAutocompleteV2
                        name='organization'
                        label='Đơn vị'
                        api={pagingAllOrg}
                    />
                  </Grid>

                  <Grid item xs={12} sm={6} md={4} lg={3}>
                    <GlobitsPagingAutocompleteV2
                        name='positionTitle'
                        label='Chức danh'
                        api={pagingPositionTitle}
                        searchObject={{
                          departmentId:values?.department?.id,
                          departmentIds:values?.departmentIds,
                        }}
                    />
                  </Grid>
                  <Grid item xs={12}>
                    <TreeAutocompleteSelector
                        label={"Phòng ban"}
                        name='departments'
                        api={pagingDepartmentHierarchy}
                        // api={pagingAllDepartments}
                        hasChild={true}
                        searchObject={{
                          pageIndex:1,
                          pageSize:9999,
                          keyword:"",
                          organizationId:values?.organization?.id,
                        }}
                        handleChange={(e, value) => {
                          let departmentIds = value?.map ((item) => item.id);
                          setFieldValue ("departmentIds", departmentIds);
                        }}
                        placeholder={"Chọn phòng ban"}
                        getOptionLabel={(option) => {
                          return option?.code? `${option?.name} - ${option?.code}` : option?.name;
                        }}
                        multiple
                    />
                  </Grid>
                </Grid>
                <div className='py-8 mt-12 border-bottom-fade border-top-fade'>
                  <div className='flex justify-end'>
                    <ButtonGroup color='container' aria-label='outlined primary button group'>
                      <Button startIcon={<SearchIcon/>} type='submit'>
                        Tìm kiếm
                      </Button>
                      <Button onClick={handleResetFilter} startIcon={<RotateLeftIcon/>}>
                        Đặt lại
                      </Button>
                      <Button
                          type='button'
                          onClick={handleCloseFilter}
                          startIcon={<HighlightOffIcon/>}>
                        Đóng bộ lọc
                      </Button>
                    </ButtonGroup>
                  </div>
                </div>
              </div>
            </Grid>
          </Grid>
        </div>
      </Collapse>
  );
}

export default memo (observer (StaffFilter));
