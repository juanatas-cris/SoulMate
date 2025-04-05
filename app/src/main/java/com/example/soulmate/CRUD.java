package com.example.soulmate;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CRUD extends AppCompatActivity {
    EditText editId, editProductName, editPrice, editQuantity, editShoeSize;
    Button btnCreate, btnRead, btnUpdate, btnDelete;
    TextView textView;
    RequestQueue requestQueue;



    String URL = "http://192.168.100.135/soulmate/";
    ArrayList<String> productList = new ArrayList<>(); // List to manage products locally

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crud);

        editId = findViewById(R.id.editId);

        editProductName = findViewById(R.id.editProductName);
        editPrice = findViewById(R.id.editPrice);
        editQuantity = findViewById(R.id.editQuantity);
        editShoeSize = findViewById(R.id.editShoeSize);
        btnCreate = findViewById(R.id.btnCreate);
        btnRead = findViewById(R.id.btnRead);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnDelete);
        textView = findViewById(R.id.textView);

        requestQueue = Volley.newRequestQueue(this);

        btnCreate.setOnClickListener(view -> createProduct());
        btnRead.setOnClickListener(view -> readProducts());
        btnUpdate.setOnClickListener(view -> updateProduct());
        btnDelete.setOnClickListener(view -> deleteProduct());
        textView.setOnClickListener(view -> selectProduct());

        readProducts(); // Auto-fetch data on start
    }

    private boolean validateFields() {
        if (editProductName.getText().toString().trim().isEmpty() ||
                editPrice.getText().toString().trim().isEmpty() ||
                editQuantity.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "❌ Please fill all fields!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void createProduct() {
        if (!validateFields()) return;

        String checkUrl = URL + "check_duplicate.php";

        StringRequest checkRequest = new StringRequest(Request.Method.POST, checkUrl,
                response -> {
                    if (response.equalsIgnoreCase("duplicate")) {
                        Toast.makeText(this, "❌ Product Name already exists!", Toast.LENGTH_SHORT).show();
                    } else {
                        insertProduct(); // Proceed with insertion
                    }
                },
                error -> Toast.makeText(this, "❌ Error: " + error.getMessage(), Toast.LENGTH_LONG).show()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("product_name", editProductName.getText().toString().trim());
                return params;
            }
        };

        requestQueue.add(checkRequest);
    }

    private void insertProduct() {
        StringRequest request = new StringRequest(Request.Method.POST, URL + "insert.php",
                response -> {
                    Toast.makeText(this, "✅ Product Added!", Toast.LENGTH_SHORT).show();
                    readProducts();
                },
                error -> Toast.makeText(this, "❌ Error: " + error.getMessage(), Toast.LENGTH_LONG).show()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("product_name", editProductName.getText().toString().trim());
                params.put("price", editPrice.getText().toString().trim());
                params.put("quantity", editQuantity.getText().toString().trim());
                return params;
            }
        };

        requestQueue.add(request);
    }

    private void readProducts() {
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, URL + "fetch.php",
                null,
                response -> {
                    productList.clear();
                    StringBuilder data = new StringBuilder();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject product = response.getJSONObject(i);
                            String productData = product.getString("id") + " - " +
                                    product.getString("product_name") +
                                    " (₱" + product.getString("price") +
                                    ", Qty: " + product.getString("quantity") + ")";
                            productList.add(productData);
                            data.append(productData).append("\n");
                        } catch (JSONException e) {
                            textView.setText("❌ Error parsing data.");
                            return;
                        }
                    }
                    textView.setText(data.toString());
                },
                error -> Toast.makeText(this, "❌ Error: " + error.getMessage(), Toast.LENGTH_SHORT).show());

        requestQueue.add(request);
    }

    private void updateProduct() {
        if (!validateFields()) return;

        AlertDialog.Builder confirmBuilder = new AlertDialog.Builder(this);
        confirmBuilder.setTitle("⚠️ Confirm Update");
        confirmBuilder.setMessage("Are you sure you want to update this product?");
        confirmBuilder.setPositiveButton("Yes", (dialog, which) -> performUpdate());
        confirmBuilder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
        confirmBuilder.show();
    }

    private void performUpdate() {
        StringRequest request = new StringRequest(Request.Method.POST, URL + "update.php",
                response -> {
                    Toast.makeText(this, "✅ Product Updated!", Toast.LENGTH_SHORT).show();
                    readProducts();
                },
                error -> Toast.makeText(this, "❌ Error: " + error.getMessage(), Toast.LENGTH_LONG).show()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", editId.getText().toString().trim());
                params.put("product_name", editProductName.getText().toString().trim());
                params.put("price", editPrice.getText().toString().trim());
                params.put("quantity", editQuantity.getText().toString().trim());
                return params;
            }
        };
        requestQueue.add(request);
    }

    private void deleteProduct() {
        String productId = editId.getText().toString().trim();

        if (productId.isEmpty()) {
            Toast.makeText(this, "❌ Please enter a Product ID to delete!", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder confirmBuilder = new AlertDialog.Builder(this);
        confirmBuilder.setTitle("⚠️ Confirm Delete");
        confirmBuilder.setMessage("Are you sure you want to delete this product from view? (Data in DB will remain)");
        confirmBuilder.setPositiveButton("Yes", (dialog, which) -> removeFromResults(productId));
        confirmBuilder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
        confirmBuilder.show();
    }

    // Remove product locally only
    private void removeFromResults(String productId) {
        StringBuilder newData = new StringBuilder();

        for (String product : productList) {
            if (!product.startsWith(productId + " - ")) {
                newData.append(product).append("\n");
            }
        }

        textView.setText(newData.toString().trim());
        Toast.makeText(this, "✅ Product removed from view (Not deleted from DB)!", Toast.LENGTH_SHORT).show();
    }

    private void selectProduct() {
        String selectedData = textView.getText().toString().trim();
        if (selectedData.isEmpty()) {
            Toast.makeText(this, "❌ No product available!", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] lines = selectedData.split("\n");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("\uD83D\uDC5F Select Shoes");
        builder.setItems(lines, (dialog, which) -> {
            String selectedLine = lines[which];
            if (selectedLine.contains(" - ")) {
                String[] parts = selectedLine.split(" - ");
                if (parts.length >= 2) {
                    editId.setText(parts[0]);
                    String[] details = parts[1].split(" \\(₱");

                    if (details.length >= 2) {
                        editProductName.setText(details[0]);
                        String[] priceQty = details[1].split(", Qty: ");
                        if (priceQty.length >= 2) {
                            editPrice.setText(priceQty[0]);
                            editQuantity.setText(priceQty[1].replace(")", ""));
                        }
                    }
                    Toast.makeText(this, "✅ Product Loaded!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.show();
    }
}
