// Ví dụ sử dụng với positionStore
import { Button } from "@material-ui/core";
import { useStore } from "app/stores";
import PositionFilter from "app/views/Position/PositionFilter";
import { observer } from "mobx-react";
import ReusableSelectPopup from "./ReusableSelectPopup";

function PositionSelectExample() {
  const { positionStore } = useStore();
  
  // Cấu hình tên fields và methods từ store
  const storeConfig = {
    // Fields
    listDataField: "listPosition",
    selectedItemsField: "listOnDelete",
    searchObjectField: "searchObject",
    totalPagesField: "totalPages",
    totalElementsField: "totalElements",
    
    // Methods
    pagingMethod: "pagingPosition",
    handleCloseMethod: "handleClose",
    resetStoreMethod: "resetStore",
    handleChangePageMethod: "handleChangePage",
    setPageSizeMethod: "setPageSize",
    handleSelectItemsMethod: "handleSelectListDelete",
    handleSetSearchObjectMethod: "handleSetSearchObject"
  };
  
  // Cấu hình cột cho bảng vị trí
  const positionColumns = [
    {
      title: "Mã vị trí",
      field: "code",
      align: "left",
    },
    {
      title: "Tên vị trí",
      field: "name",
      align: "left",
    },
    {
      title: "Chức danh",
      field: "title",
      render: data => data?.title?.name,
      align: "left",
    },
    {
      title: "Đơn vị",
      field: "organization",
      render: data => data?.department?.organization?.name,
      align: "left",
    },
    {
      title: "Phòng ban",
      field: "department",
      render: data => data?.department?.name,
      align: "left",
    },
    {
      title: "Nhân viên",
      field: "staff.displayName",
      align: "left",
      render: data => {
        const displayName = data?.staff?.displayName ?? "";
        const staffCode = data?.staff?.staffCode ?? "";
        return displayName && staffCode ? `${displayName} - ${staffCode}` : displayName || staffCode || "Vacant";
      }
    }
  ];

  return (
    <div>
      <Button
        variant="contained"
        onClick={() => positionStore.openSelectMultiplePopup = true}
      >
        Chọn vị trí
      </Button>
      
      <ReusableSelectPopup
        multipleSelect = {false}
        // Props cơ bản của popup
        open={positionStore.openSelectMultiplePopup}
        onClose={positionStore.handleClose}
        onConfirm={positionStore.handleOpenConfirmAssignPopup}
        title="Lựa chọn các vị trí của nhân viên"
        confirmText="Xác nhận"
        
        // Store và cấu hình
        store={positionStore}
        storeConfig={storeConfig}
        
        // Props cho danh sách
        listProps={{
          columns: positionColumns,
          checkboxColumnTitle: "Lựa chọn",
          selectLabel: "Chọn",
          unselectLabel: "Bỏ chọn"
        }}
        
        // Props cho toolbar
        toolbarProps={{
          searchPlaceholder: "Tìm kiếm theo tên vị trí...",
          searchTooltip: "Tìm kiếm theo tên vị trí",
          showFilter: true,
          filterComponent: <PositionFilter />
        }}
      />
    </div>
  );
}

export default observer(PositionSelectExample);