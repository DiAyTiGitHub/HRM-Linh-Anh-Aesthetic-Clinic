import React, { useState } from "react";
import GlobitsTable from "../../common/GlobitsTable";
import { useStore } from "../../stores";
import { useTranslation } from "react-i18next";
import { IconButton, Icon } from "@material-ui/core";
import { observer } from "mobx-react";
import ConstantList from "../../appConfig";
import { NavLink, useHistory } from "react-router-dom";
import { Grid } from "@material-ui/core";
import MoreHorizIcon from "@material-ui/icons/MoreHoriz";
import Menu from '@material-ui/core/Menu';
import MenuItem from '@material-ui/core/MenuItem';


function MaterialButton(props) {
  const { item, isTester, canCreateDelete } = props;

  const [anchorEl, setAnchorEl] = useState();


  return (
    <div>
      {!isTester && (
        <IconButton size="small" onClick={() => props.onSelect(item, 0)}>
          <Icon fontSize="small" color="primary">
            edit
          </Icon>
        </IconButton>
      )}
      {!isTester && canCreateDelete() && (
        <IconButton size="small" onClick={() => props.onSelect(item, 1)}>
          <Icon fontSize="small" color="secondary">
            delete
          </Icon>
        </IconButton>
      )}
      <IconButton size="small" onClick={(event) => setAnchorEl(event.currentTarget)}>
        <MoreHorizIcon />
      </IconButton>
      <Menu
        id="simple-menu"
        anchorEl={anchorEl}
        keepMounted
        open={Boolean(anchorEl)}
        onClose={() => setAnchorEl(null)}
      >
        <MenuItem onClick={() => props.onSelect(item, 2)}>Task</MenuItem>
        {/* <MenuItem onClick={() => handleChangeView('kanban')}>Kan ban</MenuItem>
        <MenuItem  onClick={() => handleChangeView('group')}>Nhóm</MenuItem> */}
      </Menu>
    </div>
  );
}

export default observer(function List() {
  const { projectStore } = useStore();
  const { t } = useTranslation();
  const history = useHistory();

  const {
    projectList,
    totalPages,
    totalElements,
    rowsPerPage,
    page,
    handleChangePage,
    setRowsPerPage,
    handleDelete,
    handleSelectListProject,
    canCreateUpdateDeleteProject
  } = projectStore;

  let columns = [
    {
      title: t("general.action"),
      minWidth: "100px",
      render: (rowData) => (
        <MaterialButton
          canCreateDelete={canCreateUpdateDeleteProject}
          item={rowData}
          onSelect={(rowData, method) => {
            if (method === 0) {
              history.push(ConstantList.ROOT_PATH + `timesheet/project/${rowData?.id}`);
            } else if (method === 1) {
              handleDelete(rowData?.id);
            } else {
              if (method === 2) {
                history.push(ConstantList.ROOT_PATH + "task/kanban/" + rowData?.id);
              }
            }
          }}
        />
      ),
    },
    {
      title: t("project.name"), field: "name", minWidth: "300px", render: (item) => (
        <NavLink to={`/timesheet/project/${item.id}`} style={{ textDecoration: 'underline' }}>{item.name}</NavLink>
      )
    },
    {
      title: t("project.code"),
      field: "code",
      align: "left",
      minWidth: "200px",
    },
    {
      title: t("project.description"),
      field: "description",
      minWidth: "350px",
      render: (rowData) => (
        <>
          {rowData?.description && (
            <div dangerouslySetInnerHTML={{ __html: rowData?.description }}></div>
          )}
        </>
      ),
    },
  ];

  return (
    <Grid item xs={12}>
      <GlobitsTable
        selection={canCreateUpdateDeleteProject()}
        data={projectList}
        handleSelectList={handleSelectListProject}
        columns={columns}
        totalPages={totalPages}
        handleChangePage={handleChangePage}
        setRowsPerPage={setRowsPerPage}
        pageSize={rowsPerPage}
        pageSizeOption={[10, 25, 50]}
        totalElements={totalElements}
        page={page}
      />
    </Grid>
  );
});
