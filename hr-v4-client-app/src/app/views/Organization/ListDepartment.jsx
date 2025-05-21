import { Button, ButtonGroup, Grid, Icon, useMediaQuery, useTheme, IconButton, Tooltip } from "@material-ui/core";
import AddIcon from "@material-ui/icons/Add";
import Config from "app/common/GlobitsConfigConst";
import { useFormikContext } from "formik";
import { useTranslation } from "react-i18next";
import DepartmentPopup from "./DepartmentPopup";
import GlobitsTable from "app/common/GlobitsTableNotPagination";
import { useState } from "react";
import DepartmentForm from "../Department/DepartmentForm";

function MaterialButton(props) {
  const { item } = props;
  return (
    <div>
      <IconButton size='small' onClick={() => props.onSelect(item, 0)}>
        <Icon fontSize='small' color='primary'>
          edit
        </Icon>
      </IconButton>
      <IconButton size='small' onClick={() => props.onSelect(item, 1)}>
        <Icon fontSize='small' color='secondary'>
          delete
        </Icon>
      </IconButton>
    </div>
  );
}

export default function ListDepartment() {
  const handleAddDepartment = () => {
    console.log("Add Department");
  };

  const { t } = useTranslation();
  const { values, setFieldValue } = useFormikContext();

  const [openEditDepartment, setOpenEditDepartment] = useState(null);

  const columns = [
    {
      title: t("general.action"),
      ...Config.tableCellConfig,
      render: (rowData) => (
        <MaterialButton
          item={rowData}
          onSelect={(rowData, method) => {
            if (method === 0) handleEditDepartment(rowData, values.departments.indexOf(rowData));
            else if (method === 1) handleDelete(values.departments.indexOf(rowData));
          }}
        />
      ),
      maxWidth: "100px",
      align: "center",
    },
    {
      title: t("department.code"),
      field: "code",
      ...Config.tableCellConfig,
    },
    {
      title: "Tên phòng ban",
      field: "name",
      ...Config.tableCellConfig,
    },
    {
      title: t("department.shortName"),
      field: "shortName",
      width: "10%",
      ...Config.tableCellConfig,
      render: (data) => data?.shortName,
    },
    {
      title: t("department.description"),
      field: "description",
      ...Config.tableCellConfig,
    },
  ];

  const handleEditDepartment = (department, rowIndex) => {
    console.log("🔥🔥🔥 ~ handleEditDepartment ~ rowIndex:", rowIndex);
    let listCurrentDepartments = values?.departments || [];
    const formData = { ...department, rowIndex };
    if (!formData?.departmentIndex) {
      formData.departmentIndex = listCurrentDepartments?.length + 1;
    }
    setOpenEditDepartment({
      open: true,
      formData,
      handleClose: () => setOpenEditDepartment(null),
      handleSubmit: (departmentValues) => {
        if (rowIndex === undefined) {
          setFieldValue("departments", [...listCurrentDepartments, departmentValues]);
        } else {
          const newList = listCurrentDepartments?.reduce((acc, item, index) => {
            if (index === rowIndex) {
              return [...acc, departmentValues];
            }
            return [...acc, item];
          }, []);
          setFieldValue("departments", newList);
        }
        setOpenEditDepartment(null);
      },
    });
  };

  const handleDelete = (index) => {
    const updatedDepartments = values.departments.filter((_, i) => i !== index);
    setFieldValue("departments", updatedDepartments);
  };

  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down("sm"));

  return (
    <Grid container spacing={2}>
      <Grid item md={12}>
        <ButtonGroup color='container' aria-label='outlined primary button group'>
          <Tooltip
            arrow
            placement="top"
            title="Thêm phòng ban thuộc đơn vị"
          >
            <Button startIcon={<AddIcon />} onClick={() => handleEditDepartment()}>
              {!isMobile && t("general.button.add")}
            </Button>
          </Tooltip>
        </ButtonGroup>
      </Grid>

      <Grid item md={12}>
        <GlobitsTable
          title='Danh sách các phòng ban'
          columns={columns}
          data={values.departments || []}
          noPagination
        />
      </Grid>

      {openEditDepartment?.open &&
        <DepartmentPopup {...openEditDepartment} />
      }
    </Grid>
  );
}
