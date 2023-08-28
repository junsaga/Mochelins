package com.musthave0145.mochelins;

import static android.content.Context.LOCATION_SERVICE;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.musthave0145.mochelins.api.NetworkClient;
import com.musthave0145.mochelins.api.PlaceApi;
import com.musthave0145.mochelins.config.Config;
import com.musthave0145.mochelins.model.MarkerItem;
import com.musthave0145.mochelins.model.Place;
import com.musthave0145.mochelins.model.PlaceList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * A simple {@link Fragment} subclass.

 * create an instance of this fragment.
 */
public class MapFragment extends Fragment  implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private Map<Marker,MarkerItem> markerItemMap = new HashMap<>();
    double lat=37.542127;
    double lng=126.680545;
    boolean isLocationReady;

    ArrayList<Place> placeArrayList = new ArrayList<>();

    int radius =2000;
    String pagetoken;
    private OnMapReadyCallback callback = new OnMapReadyCallback() {
        LocationManager locationManager;
        LocationListener locationListener;
        private Marker selectedMarker; // 선택된 마커를 저장하기 위한 변수
        private View placeRowView; // place_row 레이아웃을 동적으로 생성하기 위한 변수
        private TextView txtName;
        private TextView txtVicinity;


        String keyword;

        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;




            Retrofit retrofit = NetworkClient.getRetrofitClient(getActivity());
            PlaceApi api = retrofit.create(PlaceApi.class);

            Call<PlaceList> call = api.getPlaceList("ko",lat+","+lng,
                    radius, Config.GOOGLE_API_KEY,"식당");

            call.enqueue(new Callback<PlaceList>() {
                @Override
                public void onResponse(Call<PlaceList> call, Response<PlaceList> response) {
                    PlaceList placeList = response.body();



                    placeArrayList.addAll( placeList.results );

                    for(Place place : placeArrayList){
                        LatLng location = new LatLng(place.geometry.location.lat,place.geometry.location.lng);
                        // 커스텀 마커 레이아웃 설정
                        View customMarkerView = LayoutInflater.from(getActivity()).inflate(R.layout.marker_layout, null);
                        ImageView imageView = customMarkerView.findViewById(R.id.imageView);
                        TextView textView = customMarkerView.findViewById(R.id.textView);

                        imageView.setImageResource(R.drawable.baseline_star_24);
                        textView.setText(place.rating);
                        BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(viewToBitmap(customMarkerView));
                        MarkerOptions markerOptions = new MarkerOptions()
                                .position(location)
                                .title(place.name)
                                .icon(icon);

                        Marker marker = mMap.addMarker(markerOptions);
                        markerItemMap.put(marker, new MarkerItem(place.name, location));
                    }
                    // 받아온 데이터를 첫 번째 장소로 설정하여 카드뷰 업데이트
                    updateCardViewData(placeArrayList.get(0));
                }

                private Bitmap viewToBitmap(View view) {
                    view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                    Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(bitmap);
                    view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
                    view.draw(canvas);
                    return bitmap;
                }

                private String unescapeUnicode(String input) {
                    if (input == null) {
                        return "";
                    }

                    StringBuilder result = new StringBuilder();
                    int length = input.length();
                    int i = 0;
                    while (i < length) {
                        char currentChar = input.charAt(i);
                        if (currentChar == '\\' && i + 1 < length && input.charAt(i + 1) == 'u') {
                            // Check if the next characters form a Unicode escape sequence
                            try {
                                int codePoint = Integer.parseInt(input.substring(i + 2, i + 6), 16);
                                result.append((char) codePoint);
                                i += 6; // Skip the whole escape sequence
                            } catch (NumberFormatException e) {
                                // Invalid escape sequence, just append as-is
                                result.append(currentChar);
                                i++;
                            }
                        } else {
                            result.append(currentChar);
                            i++;
                        }
                    }
                    return result.toString();
                }
                private void updateCardViewData(Place place) {
                    CardView cardView = rootView.findViewById(R.id.cardView);
                    if (cardView != null) {
                        txtName = cardView.findViewById(R.id.txtVicinity);
                        txtVicinity = cardView.findViewById(R.id.txtVicinity);
                        if (txtName != null && txtVicinity != null) {
                            String unescapedName = unescapeUnicode(place.name);
                            String unescapedVicinity = unescapeUnicode(place.Vicinity);
                            txtName.setText(unescapedName);
                            txtVicinity.setText(unescapedVicinity);
                        }
                    }
                }
                @Override
                public void onFailure(Call<PlaceList> call, Throwable t) {

                }
            });

            //cardView를 rootView를 기반으로 찾습니다.
            CardView cardView = rootView.findViewById(R.id.cardView);

            //맵 클릭 시 카드 뷰 숨기기
            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    // 카드뷰 가시성을 VISIBLE로 변경
                    cardView.setVisibility(View.VISIBLE);

                    MarkerItem markerItem =markerItemMap.get(marker);
                    if(markerItem != null){
                        // 마커와 연관된 Place 객체를 찾아서 카드뷰 데이터 업데이트
                        for(Place place :placeArrayList){
                            if(place.name.equals(markerItem.getTitle())) {
                                updateCardViewData(place);
                                break;
                            }
                        }
                    }

                    return true;
                }

                private void updateCardViewData(Place place) {
                    CardView cardView = rootView.findViewById(R.id.cardView);
                    txtName = cardView.findViewById(R.id.txtVicinity);
                    txtVicinity = cardView.findViewById(R.id.txtVicinity);
                    String unescapedName = unescapeUnicode(place.name);
                    String unescapedVicinity = unescapeUnicode(place.Vicinity);

                    txtName.setText(place.name);
                    txtVicinity.setText(place.Vicinity);
                }

                private String unescapeUnicode(String input) {
                    if (input == null) {
                        return "";
                    }

                    StringBuilder result = new StringBuilder();
                    int length = input.length();
                    int i = 0;
                    while (i < length) {
                        char currentChar = input.charAt(i);
                        if (currentChar == '\\' && i + 1 < length && input.charAt(i + 1) == 'u') {
                            // Check if the next characters form a Unicode escape sequence
                            try {
                                int codePoint = Integer.parseInt(input.substring(i + 2, i + 6), 16);
                                result.append((char) codePoint);
                                i += 6; // Skip the whole escape sequence
                            } catch (NumberFormatException e) {
                                // Invalid escape sequence, just append as-is
                                result.append(currentChar);
                                i++;
                            }
                        } else {
                            result.append(currentChar);
                            i++;
                        }
                    }
                    return result.toString();
                }
            });

            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    // 카드뷰 가시성을 GONE으로 변경
                    cardView.setVisibility(View.GONE);

                }
            });


            // 폰의 위치를 가져오기 위해서는, 시스템서비스로부터 로케이션 매니져를
            // 받아온다.
            locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    lat = location.getLatitude();
                    lng = location.getLongitude();

                    isLocationReady = true;
                    if (isLocationReady) {

                        if (getActivity() != null) {
                            LatLng currentLocation = new LatLng(37.542127, 126.680545);
                            View customMarkerView = LayoutInflater.from(getActivity()).inflate(R.layout.marker_layout, null);
                            ImageView imageView = customMarkerView.findViewById(R.id.imageView);
                            TextView textView = customMarkerView.findViewById(R.id.textView);

                            //커스텀 마커 레이아웃 설정
                            imageView.setImageResource(R.drawable.baseline_star_24);
                            textView.setText("3");
                            BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(viewToBitmap(customMarkerView));


                            //커스텀 마커 레이아웃을 사용한 MarkerOption 생성
                            MarkerOptions markerOptions = new MarkerOptions()
                                    .position(currentLocation)
                                    .title("Current Location")
                                    .icon(icon);
                            // GoogleMap에 마커 추가
                            Marker marker = googleMap.addMarker(markerOptions);
                            marker.showInfoWindow();

                            googleMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
                        }
                    }

                }

                private Bitmap viewToBitmap(View view) {
                    view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                    Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(bitmap);
                    view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
                    view.draw(canvas);
                    return bitmap;
                }
            };
            if( ActivityCompat.checkSelfPermission(getActivity(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED ){

                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                                android.Manifest.permission.ACCESS_COARSE_LOCATION} ,
                        100);
                return;
            }
            // 위치기반 허용하였으므로,
            // 로케이션 매니저에, 리스너를 연결한다. 그러면 동작한다.
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    3000,
                    -1,
                    locationListener);


        }
    };


    private View rootView;
    private View placeRowView;
    private EditText searchEditText;
    private ImageButton searchButton;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_map, container, false);



        return rootView;
    }
    // 내 위치 가져오기 위한 멤버변수

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }

    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        return false;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

    }

}