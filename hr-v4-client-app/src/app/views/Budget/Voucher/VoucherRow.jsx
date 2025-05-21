const { Delete } = require("@material-ui/icons");
const { default: GlobitsPagingAutocompleteV2 } = require("app/common/form/GlobitsPagingAutocompleteV2");
const { default: GlobitsVNDCurrencyInput } = require("app/common/form/GlobitsVNDCurrencyInput");
const { memo, useState } = require("react");
const { useTranslation } = require("react-i18next");
const { pagingBudgetCategory } = require("../BudgetCategory/BudgetCategoryService");
const { default: GlobitsTextField } = require("app/common/form/GlobitsTextField");
const { default: GlobitsConfirmationDialog } = require("app/common/GlobitsConfirmationDialog");

const VoucherRow = memo(({ index, voucherItems, remove, push, nameSpace, disabled }) => {
  const { t } = useTranslation();

  const withNameSpace = (field) => {
    if (field) return `${nameSpace}.${field}`;
    return nameSpace;
  };

  const [openConfirmDeletePopup, setOpenConfirmDeletePopup] = useState(false);

  function handleConfirmDeleteItem() {
    // setOpenConfirmDeletePopup(true);
    remove();
  }

  return (
    <>
      <tr className='row-table-body' key={index}>
        {!disabled && (
          <td align='center'>
            <span className='pointer tooltip' style={{ cursor: "pointer" }} onClick={() => setOpenConfirmDeletePopup(true)}>
              <Delete className='text-red' />
            </span>
          </td>
        )}
        <td>
          <GlobitsVNDCurrencyInput name={withNameSpace("amount")} />
        </td>
        <td>
          <GlobitsPagingAutocompleteV2
            name={withNameSpace("budgetCategory")}
            api={pagingBudgetCategory}
            required // Mark "Loại khoản" as required in the UI
          />
        </td>
        <td>
          <GlobitsTextField isTextArea={true} multiline name={withNameSpace("note")} />
        </td>
      </tr>

      <GlobitsConfirmationDialog
        open={openConfirmDeletePopup}
        onConfirmDialogClose={() => setOpenConfirmDeletePopup(false)}
        onYesClick={handleConfirmDeleteItem}
        title={t("confirm_dialog.delete.title")}
        text={"Bạn có chắc muốn xóa chứng chỉ này?"}
        agree={t("confirm_dialog.delete.agree")}
        cancel={t("confirm_dialog.delete.cancel")}
      />
    </>
  );
});

export default VoucherRow;