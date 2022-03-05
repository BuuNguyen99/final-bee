package com.example.hairstyle.service.implementation;

import com.example.hairstyle.constant.PaymentConfig;
import com.example.hairstyle.constant.ResponseText;
import com.example.hairstyle.entity.*;
import com.example.hairstyle.payload.request.PaymentRequest;
import com.example.hairstyle.payload.request.ProductRequest;
import com.example.hairstyle.payload.request.RateRequest;
import com.example.hairstyle.payload.request.SearchQuery;
import com.example.hairstyle.payload.response.MessageResponse;
import com.example.hairstyle.payload.response.PagingResponse;
import com.example.hairstyle.payload.response.PaymentResponse;
import com.example.hairstyle.repository.*;
import com.example.hairstyle.service.ProductService;
import com.example.hairstyle.util.*;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    private final ProfileRepository profileRepository;

    private final RateRepository rateRepository;

    private final BehaviorRepository behaviorRepository;

    private final ImageRepository imageRepository;

    @Override
    public ResponseEntity getAll(SearchQuery searchQuery, int page, int size) {
        Page<Product> productPages;

        if (Objects.nonNull(searchQuery)) {
            var paging = PagingUtils.getPageRequest(searchQuery, page, size);
            var spec = SpecificationUtils.bySearchQuery(searchQuery, Product.class);
            productPages = productRepository.findAll(spec, paging);
        } else {
            var paging = PageRequest.of(page, size);
            productPages = productRepository.findAll(paging);
        }

        if (productPages.isEmpty()) {
            return ResponseEntity
                    .ok()
                    .body(new ArrayList<>());
        }

        var resultList = new PagingResponse<Product>();
        resultList.setContent(productPages.getContent());
        resultList.setCurrentPage(productPages.getNumber());
        resultList.setTotalItems(productPages.getTotalElements());
        resultList.setTotalPages(productPages.getTotalPages());

        return ResponseEntity
                .ok(resultList);
    }

    @Override
    @Transactional
    public ResponseEntity addProduct(ProductRequest request) {
        var slug = SlugUtils.makeSlug(request.getTitle());
        var productOptional = productRepository.findBySlug(slug);

        if (productOptional.isPresent()) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse(false, ResponseText.EXISTED_PRODUCT));
        }

        var product = new Product();
        product.setTitle(request.getTitle());
        product.setMetaTitle(request.getMetaTitle());
        product.setDiscount(request.getDiscount());
        product.setContent(request.getContent());
        product.setSummary(request.getSummary());
        product.setPrice(request.getPrice());
        product.setQuantity(request.getQuantity());
        product.setSlug(slug);
        product.setAverageRating(5d);
        product.setCategory(request.getCategory());
        if(!request.getImageUrls().isEmpty()){
            request.getImageUrls().forEach(imageUrl->{
                var image = new Image();
                image.setProduct(product);
                image.setUrl(imageUrl);
                product.getImages().add(image);
            });
        }

        productRepository.save(product);
        imageRepository.saveAll(product.getImages());

        return ResponseEntity
                .ok(product);
    }

    @Override
    @Transactional
    public ResponseEntity removeProducts(Set<Long> ids) {
        var products = productRepository.findAllById(ids);

        if(!products.iterator().hasNext()){
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse(false, ResponseText.NO_DATA_RETRIEVAL));
        }
        var imageList = new HashSet<Image>();

        products.forEach(product -> {
            imageList.addAll(product.getImages());
            product.getImages().clear();
            product.setIsDeleted(true);
        });

        imageRepository.deleteAll(imageList);
        //productRepository.saveAll(products);
        productRepository.deleteAll(products);
        return ResponseEntity
                .ok(new MessageResponse(false, ResponseText.DELETE_SUCCESSFULLY));
    }

    @Override
    @Transactional
    public ResponseEntity updateProduct(ProductRequest request, Long id) {
        var productOptional = productRepository.findById(id);
        if (productOptional.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse(false, ResponseText.UNFOUNDED_PRODUCT));
        }

        var product = productOptional.get();

        if (StringUtils.hasText(request.getTitle())) {
            product.setTitle(request.getTitle());
            product.setSlug(SlugUtils.makeSlug(request.getTitle()));
        }

        if (StringUtils.hasText(request.getCategory())) {
            product.setCategory(request.getCategory());
        }

        if (StringUtils.hasText(request.getMetaTitle())) {
            product.setMetaTitle(request.getMetaTitle());
        }

        if (StringUtils.hasText(request.getSummary())) {
            product.setSummary(request.getSummary());
        }

        if (Objects.nonNull(request.getDiscount())) {
            product.setDiscount(request.getDiscount());
        }

        if (Objects.nonNull(request.getPrice())) {
            product.setPrice(request.getPrice());
        }

        if (Objects.nonNull(request.getQuantity())) {
            product.setQuantity(request.getQuantity());
        }

        if (StringUtils.hasText(request.getContent())) {
            product.setContent(request.getContent());
        }

        if(!request.getImageUrls().isEmpty()){
            var oldImages = product.getImages();
            imageRepository.deleteAll(oldImages);
            product.getImages().clear();
            request.getImageUrls().forEach(imageUrl->{
                var image = new Image();
                image.setProduct(product);
                image.setUrl(imageUrl);
                product.getImages().add(image);
            });
        }

        imageRepository.saveAll(product.getImages());
        productRepository.save(product);

        return ResponseEntity
                .ok(product);
    }

    @Override
    public ResponseEntity getDetailProduct(String slug, String username) {
        var productOptional = productRepository.findBySlug(slug);

        if (productOptional.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse(false, ResponseText.NO_DATA_RETRIEVAL));
        }

        var product = productOptional.get();

        var profileOptional = profileRepository.findByAccount_Username(username);

        if (profileOptional.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse(false, ResponseText.UNFOUNDED_PROFILE));
        }

        var profile = profileOptional.get();
        var behaviorOptional = profile.getBehaviors().stream().filter(behavior -> product.equals(behavior.getProduct())).findAny();
        if (behaviorOptional.isEmpty()) {
            var newBehavior = new Behavior();
            newBehavior.setTime(1);
            newBehavior.setProduct(product);
            newBehavior.setProfile(profile);

            behaviorRepository.save(newBehavior);

        } else {
            var behavior = behaviorOptional.get();
            behavior.setTime(behavior.getTime() + 1);

            behaviorRepository.save(behavior);
        }

        return ResponseEntity
                .ok(product);
    }

    @Override
    @Transactional
    public ResponseEntity rateProduct(RateRequest rateRequest, String username) {
        var productOptional = productRepository.findById(rateRequest.getId());

        if (productOptional.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse(false, ResponseText.UNFOUNDED_PRODUCT));
        }

        var profileOptional = profileRepository.findByAccount_Username(username);

        if (profileOptional.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse(false, ResponseText.UNFOUNDED_PROFILE));
        }

        var profile = profileOptional.get();
        var product = productOptional.get();

        var rateOptional = rateRepository.findByProfile_IdAndProduct_Id(profile.getId(), product.getId());
        double average;
        if (rateOptional.isPresent()) {
            var rate = rateOptional.get();
            average = CalculateUtils.replaceRate(product.getRatings().size(),
                    product.getAverageRating(),
                    rate.getRating(),
                    rateRequest.getRating());

            rate.setRating(rateRequest.getRating());

            product.setAverageRating(average);
            rate.setRating(rateRequest.getRating());
            if (StringUtils.hasText(rateRequest.getContent())) {
                rate.setContent(rateRequest.getContent());
            }

            rateRepository.save(rate);

            return ResponseEntity
                    .ok(rate);

        } else {
            average = CalculateUtils.addRate(product.getRatings().size(),
                    product.getAverageRating(),
                    rateRequest.getRating());
            product.setAverageRating(average);

            var newRate = new Rating();
            newRate.setRating(rateRequest.getRating());
            newRate.setPublished(true);
            newRate.setContent(rateRequest.getContent());
            newRate.addProduct(product);
            newRate.addProfile(profile);

            rateRepository.save(newRate);

            return ResponseEntity
                    .ok(newRate);
        }
    }

    @Override
    public ResponseEntity favoriteProduct(String username) {
        var productIterable = productRepository.findAll();
        var profiles = profileRepository.findAllByBehavior();

        var products = Lists.newArrayList(productIterable);

        var matrix = generateRatingMatrix(products, profiles);

        var profileOptional = profileRepository.findByAccount_Username(username);

        if (profileOptional.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse(false, ResponseText.UNFOUNDED_PROFILE));
        }

        var profile = profileOptional.get();
        int u = profiles.indexOf(profile);
        double aRU = getAverageRating(u, matrix);

        List<Pair> userSimilarities = new ArrayList<>();
        for (int v = 0; v < profiles.size(); v++) {
            if (v != u) {
                double aRV = getAverageRating(v, matrix);
                var value = userSimilarityCalculate(u, aRU, v, aRV, matrix, products, profile, profiles.get(v));
                if (value != 0) {
                    userSimilarities.add(new Pair(v, value));
                }
            }
        }

        Collections.sort(userSimilarities);

        if (userSimilarities.size() > 30) {
            userSimilarities = userSimilarities.subList(0, 30);
        }

        var listItems = new ArrayList<Integer>();

        userSimilarities.forEach(user -> {
            for (int i = 0; i < matrix[0].length; i++) {
                if (matrix[user.getIndex()][i] != 0 && !listItems.contains(i)) {
                    listItems.add(i);
                }
            }
        });

        var prediction = predictionRating(aRU, userSimilarities, listItems, matrix);
        var result = prediction.stream().sorted(Comparator.comparing(Pair::getValue)).map(pair -> products.get(pair.getIndex())).collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    private double[][] generateRatingMatrix(List<Product> products, List<Profile> profiles) {
        int m = profiles.size();
        int n = products.size();
        var ratingMatrix = new double[m][n];

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                var profile = profiles.get(i);
                var product = products.get(j);
                var ratingOptional = profile.getRatings().stream().filter(rating -> product.equals(rating.getProduct())).findFirst();
                if (ratingOptional.isPresent()) {
                    var rating = ratingOptional.get();
                    ratingMatrix[i][j] = rating.getRating();
                } else {
                    ratingMatrix[i][j] = 0d;
                }
            }
        }
        return ratingMatrix;
    }

    private double getAverageRating(int indexOfUser, double[][] matrix) {
        double sum = 0d;
        for (int i = 0; i < matrix[0].length; i++) {
            sum += matrix[indexOfUser][i];
        }
        return sum / matrix[0].length;
    }

    private double userSimilarityCalculate(int u, Double aRU, int v, Double aRV, double[][] matrix, List<Product> products, Profile user, Profile user2) {
        double numerator = 0d;
        double denominator1 = 0d;
        double denominator2 = 0d;
        for (int i = 0; i < matrix[0].length; i++) {
            var wu = TF_IDF(user, products.get(i), matrix.length);
            var wv = TF_IDF(user2, products.get(i), matrix.length);
            numerator += (matrix[u][i] * wu - aRU) * (matrix[v][i] * wv - aRV);
            denominator1 += (matrix[u][i] * wu - aRU) * (matrix[u][i] * wu - aRU);
            denominator2 += (matrix[v][i] * wv - aRV) * (matrix[v][i] * wv - aRV);

        }

        return numerator / Math.sqrt(denominator1 * denominator2);
    }

    private double TF_IDF(Profile user, Product product, int userLength) {
        var behaviorOptional = user.getBehaviors().stream().filter(b -> b.getProduct().equals(product)).findFirst();
        if (behaviorOptional.isEmpty()) {
            return 0;
        }
        var behavior = behaviorOptional.get();
        var total = user.getBehaviors().stream().map(Behavior::getTime).reduce(0, Integer::sum);
        if (total == 0) {
            return 0;
        }
        var tf = behavior.getTime() / total;
        var pop = product.getBehaviorItems().stream().map(Behavior::getTime).reduce(0, Integer::sum);
        var idf = Math.log10((double) userLength / (1 + pop));
        return tf * idf;
    }

    private ArrayList<Pair> predictionRating(double aRU, List<Pair> profiles, List<Integer> products, double[][] matrix) {
        var result = new ArrayList<Pair>();
        for (Integer product : products) {  //product indexes
            double numerator = 0d;
            double denominator = 0d;
            for (Pair profile : profiles) {
                var aRV = getAverageRating(profile.getIndex(), matrix);
                numerator += profile.getValue() * (matrix[profile.getIndex()][product] - aRV);
                denominator += Math.abs(profile.getValue());
            }
            result.add(new Pair(product, aRU + numerator / denominator));
        }
        return result;
    }

    @Transactional
    @Override
    public ResponseEntity createPayment(PaymentRequest paymentRequest) throws IOException {
        var codeConfig = new CodeConfig(8, CodeConfig.Charset.NUMBERS,null,null,null);
        var vnp_TxnRef = CodeGeneration.generate(codeConfig);// mã đơn hàng

        int amount = paymentRequest.getAmount() * 100;
        var vnpParams = new HashMap<>();
        vnpParams.put("vnp_Version", PaymentConfig.VPN_VERSION);
        vnpParams.put("vnp_Command", PaymentConfig.VNP_COMMAND);
        vnpParams.put("vnp_TmnCode", PaymentConfig.TMN_CODE);
        vnpParams.put("vnp_Amount", String.valueOf(amount));
        vnpParams.put("vnp_CurrCode", PaymentConfig.CURRENCY_CODE);
        String bankCode = paymentRequest.getBankCode();
        if (StringUtils.hasText(bankCode)) {
            vnpParams.put("vnp_BankCode", bankCode);
        }
        vnpParams.put("vnp_TxnRef", vnp_TxnRef);
        vnpParams.put("vnp_OrderInfo", paymentRequest.getDescription());
        vnpParams.put("vnp_OrderType", PaymentConfig.ORDER_TYPE);
        vnpParams.put("vnp_Locale", PaymentConfig.DEFAULT_LOCALE);
        vnpParams.put("vnp_ReturnUrl", PaymentConfig.RETURN_URL);
        vnpParams.put("vnp_IpAddr", PaymentConfig.DEFAULT_IP);

        var cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        var formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        var vnpCreateDate = formatter.format(cld.getTime());
        vnpParams.put("vnp_CreateDate", vnpCreateDate);

        var fieldNames = new ArrayList(vnpParams.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

        var itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) vnpParams.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                //Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                //Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));

                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String vnpSecureHash = VNPUtils.Sha256(PaymentConfig.CHECKSUM + hashData);
        queryUrl += "&vnp_SecureHashType=SHA256&vnp_SecureHash=" + vnpSecureHash;
        String paymentUrl = PaymentConfig.VNP_URL + "?" + queryUrl;

        var response = new PaymentResponse();
        response.setMessageResponse(new MessageResponse(true,ResponseText.SUCCESSFUL_PAYMENT));
        response.setUrl(paymentUrl);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity getRating(String slug) {
        var ratings = rateRepository.findAllBySlug(slug);
        return ResponseEntity
                .ok(ratings);
    }

    static class Pair implements Comparable<Pair> {
        private Integer index;
        private Double value;

        public Pair(Integer index, Double value) {
            this.index = index;
            this.value = value;
        }

        public Integer getIndex() {
            return index;
        }

        public void setIndex(Integer index) {
            this.index = index;
        }

        public Double getValue() {
            return value;
        }

        public void setValue(Double value) {
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Pair pair = (Pair) o;
            return Objects.equals(index, pair.index) && Objects.equals(value, pair.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(index, value);
        }

        @Override
        public int compareTo(Pair e) {
            if (value == null || e.value == null) {
                return 0;
            }
            return value.compareTo(e.value);
        }
    }
}
