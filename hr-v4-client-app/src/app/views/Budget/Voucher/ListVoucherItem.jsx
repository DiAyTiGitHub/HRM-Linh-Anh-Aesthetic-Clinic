import { Button, Grid, Icon, IconButton } from "@material-ui/core";
import { Add } from "@material-ui/icons";
import GlobitsTable from "app/common/GlobitsTableNoHeader";
import { useFormikContext } from "formik";
import React from "react";
import { useTranslation } from "react-i18next";
import VoucherItemPopup from "./VoucherItemPopup";

export default function ListVoucherItem() {
  const { t } = useTranslation();
  const { values, setFieldValue } = useFormikContext();
  const [openEditVoucherItem, setOpenEditVoucherItem] = React.useState(null);

  const columns = [
    {
      title: t("STT"),
      width: "80",
      render: (rowData, index) => rowData?.tableData?.id + 1, // Tăng chỉ số bắt đầu từ 1
      cellStyle: { textAlign: "center" },
      headerStyle: { textAlign: "center" },
    },
    {
      title: t("voucherItem.amount"),
      field: "amount",
      width: "150",
      render: (rowData) => rowData.amount?.toLocaleString("vi-VN", { style: "currency", currency: "VND" }) || "₫0", // Chuyển số thành tiền tệ
    },
    { title: t("voucherItem.note"), field: "note", width: "150", sorting: false },
    {
      title: t("voucherItem.budgetCategory"),
      field: "note",
      width: "150",
      sorting: false,
      render: (rowData) => rowData?.budgetCategory?.name || "",
    },
    {
      title: "Hành động",
      width: "150",
      render: (rowData) => (
        <div>
          <IconButton size='small' onClick={() => handleEditVoucherItem(rowData, values?.voucherItems.indexOf(rowData))}>
            <Icon fontSize='small' color='primary'>
              edit
            </Icon>
          </IconButton>
          <IconButton size='small' onClick={() => handleDelete(values?.voucherItems.indexOf(rowData))}>
            <Icon fontSize='small' color='error'>
              delete
            </Icon>
          </IconButton>
        </div>
      ),
    },
  ];

  const handleEditVoucherItem = (row, rowIndex) => {
    let listCurrentVoucherItems = values?.voucherItems || [];
    const formData = { ...row, rowIndex, voucherType: values.voucherType };

    // Đảm bảo orderIndex tồn tại
    if (!formData?.orderIndex) {
      formData.orderIndex = listCurrentVoucherItems?.length + 1;
    }

    // Khi mở popup, set giá trị `amount` là số
    setOpenEditVoucherItem({
      open: true,
      formData,
      handleClose: () => setOpenEditVoucherItem(null),
      handleSubmit: (voucherItem) => {
        // Chuyển `amount` thành số khi thêm mới hoặc cập nhật
        const updatedVoucherItem = { ...voucherItem, amount: Number(voucherItem.amount) };

        if (rowIndex === undefined) {
          setFieldValue("voucherItems", [...listCurrentVoucherItems, updatedVoucherItem]);
        } else {
          const newList = listCurrentVoucherItems.map((item, index) => (index === rowIndex ? updatedVoucherItem : item));
          setFieldValue("voucherItems", newList);
        }
        setOpenEditVoucherItem(null);
      },
    });
  };

  const handleDelete = (index) => {
    const updatedVoucherItems = values?.voucherItems.filter((_, i) => i !== index);
    setFieldValue("voucherItems", updatedVoucherItems);
  };

  return (
    <>
      <Button className='btn btn-primary d-inline-flex mb-2' startIcon={<Add />} onClick={() => handleEditVoucherItem(null)}>
        {t("voucherItem.title")}
      </Button>
      <Grid item md={12}>
        <GlobitsTable title={t("voucherItem.title")} columns={columns} data={values?.voucherItems || []} noPagination />
      </Grid>
      {openEditVoucherItem?.open && <VoucherItemPopup {...openEditVoucherItem} />}
    </>
  );
}
