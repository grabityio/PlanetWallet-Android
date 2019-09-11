package io.grabity.planetwallet.Views.p8_Tx.Activity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.Date;

import io.grabity.planetwallet.Common.commonset.C;
import io.grabity.planetwallet.Common.components.PlanetWalletActivity;
import io.grabity.planetwallet.MiniFramework.utils.Route;
import io.grabity.planetwallet.MiniFramework.utils.Utils;
import io.grabity.planetwallet.MiniFramework.wallet.cointype.CoinType;
import io.grabity.planetwallet.R;
import io.grabity.planetwallet.VO.MainItems.MainItem;
import io.grabity.planetwallet.VO.Tx;
import io.grabity.planetwallet.Widgets.FontTextView;
import io.grabity.planetwallet.Widgets.PlanetView;
import io.grabity.planetwallet.Widgets.StretchImageView;
import io.grabity.planetwallet.Widgets.ToolBar;

public class DetailTxActivity extends PlanetWalletActivity implements ToolBar.OnToolBarClickListener {

    private ViewMapper viewMapper;
    private Tx tx;
    private MainItem mainItem;

    @Override
    protected void onCreate( @Nullable Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_detail_tx );
        viewMapper = new ViewMapper( );
        viewInit( );
        setData( );
    }

    @Override
    protected void viewInit( ) {
        super.viewInit( );

        viewMapper.toolBar.setLeftButton( new ToolBar.ButtonItem( ).setTag( C.tag.TOOLBAR_BACK ) );
        viewMapper.toolBar.setOnToolBarClickListener( this );
        viewMapper.textTxID.setOnClickListener( this );
    }

    @Override
    protected void setData( ) {
        super.setData( );

        if ( getSerialize( C.bundleKey.TX ) == null ) {
            finish( );
        } else {
            tx = ( Tx ) getSerialize( C.bundleKey.TX );

            if ( getSerialize( C.bundleKey.MAIN_ITEM ) != null ) {
                mainItem = ( MainItem ) getSerialize( C.bundleKey.MAIN_ITEM );
            }
            setUpView( );
        }
    }

    void setUpView( ) {
        Utils.addressForm( viewMapper.textAddress, tx.getTo_planet( ) == null ? tx.getTo( ) : tx.getFrom( ) );
        if ( Utils.equals( tx.getStatus( ), C.transferStatus.PENDING ) ) {
            viewMapper.toolBar.setTitle( localized( R.string.transaction_status_pending_title ) );
        } else if ( Utils.equals( tx.getType( ), C.transferType.SENT ) ) {
            viewMapper.toolBar.setTitle( localized( R.string.transaction_type_sent_title ) );
        } else {
            viewMapper.toolBar.setTitle( localized( R.string.transaction_type_received_title ) );
        }

        viewMapper.groupPlanet.setVisibility( tx.getTo_planet( ) != null && tx.getFrom_planet( ) != null ? View.VISIBLE : View.GONE );
        viewMapper.groupAddress.setVisibility( tx.getTo_planet( ) != null && tx.getFrom_planet( ) != null ? View.GONE : View.VISIBLE );


        if ( tx.getTo_planet( ) != null ) {
            viewMapper.planetViewTo.setData( Utils.equals( tx.getType( ), C.transferType.RECEIVED ) ? tx.getFrom( ) : tx.getTo( ) );
            viewMapper.textPlanetName.setText( Utils.equals( tx.getType( ), C.transferType.RECEIVED ) ? tx.getFrom_planet( ) : tx.getTo_planet( ) );
            viewMapper.textPlanetToAddress.setText( Utils.equals( tx.getType( ), C.transferType.RECEIVED ) ? Utils.addressReduction( tx.getFrom( ) ) : Utils.addressReduction( tx.getTo( ) ) );
        } else {
            if ( getSerialize( C.bundleKey.MAIN_ITEM ) != null ) {
                if ( CoinType.of( mainItem.getCoinType( ) ) == CoinType.ERC20 ) {
                    ImageLoader.getInstance( ).displayImage( Route.URL( mainItem.getImg_path( ) ), viewMapper.imageCoinIcon );
                } else {
                    viewMapper.imageCoinIcon.setImageResource( R.drawable.icon_eth );
                }
            } else {
                viewMapper.imageCoinIcon.setImageResource( Utils.equals( tx.getCoin( ), CoinType.ETH.getDefaultUnit( ) ) ? R.drawable.icon_eth : R.drawable.icon_btc );
            }
        }

        viewMapper.textBalance.setText( String.format( "%s " + tx.getSymbol( ), Utils.ofZeroClear( Utils.toMaxUnit( CoinType.of( tx.getCoin( ) ), tx.getAmount( ) ) ) ) );
        viewMapper.textStatus.setText( Utils.equals( tx.getStatus( ), C.transferStatus.PENDING ) ? localized( R.string.transaction_status_pending_title ) : localized( R.string.transaction_status_completed_title ) );
        viewMapper.textAmount.setText( String.format( "%s " + tx.getSymbol( ), Utils.ofZeroClear( Utils.toMaxUnit( CoinType.of( tx.getCoin( ) ), tx.getAmount( ) ) ) ) );
        viewMapper.textFee.setText( String.format( "%s " + tx.getCoin( ), Utils.equals( tx.getCoin( ), CoinType.BTC.getDefaultUnit( ) ) ? Utils.convertUnit( tx.getFee( ), 0, Integer.valueOf( tx.getDecimals( ) ) ) : Utils.feeCalculation( Utils.convertUnit( tx.getGasPrice( ), 0, CoinType.of( tx.getCoin( ) ).getPrecision( ) ), tx.getGasLimit( ) ) ) );
        viewMapper.textTxID.setText( tx.getTx_id( ) );
        viewMapper.textTxID.underLine( );

        if ( Utils.equals( tx.getStatus( ), C.transferStatus.CONFIRMED ) )
            viewMapper.textDate.setText( Utils.dateFormat( new Date( Long.valueOf( tx.getCreated_at( ) ) * 1000 ), "yyyy. MM. dd HH:mm:ss" ) );

    }

    @Override
    public void onToolBarClick( Object tag, View view ) {
        if ( Utils.equals( tag, C.tag.TOOLBAR_BACK ) ) {
            super.onBackPressed( );
        }
    }

    @Override
    public void onClick( View v ) {
        super.onClick( v );
        if ( v == viewMapper.textTxID ) {

            sendActionUri( Utils.equals( tx.getCoin( ), CoinType.ETH.getDefaultUnit( ) ) ?
                    tx.getExplorer( ) : tx.getUrl( ) );
        }
    }

    public class ViewMapper {

        ToolBar toolBar;
        StretchImageView imageCoinIcon;
        PlanetView planetViewTo;

        ViewGroup groupPlanet;
        ViewGroup groupAddress;

        TextView textPlanetToAddress;
        TextView textPlanetName;
        TextView textAddress;
        TextView textBalance;
        TextView textStatus;
        TextView textAmount;
        TextView textFee;
        TextView textDate;
        FontTextView textTxID;


        public ViewMapper( ) {
            toolBar = findViewById( R.id.toolBar );
            imageCoinIcon = findViewById( R.id.image_detail_tx_coin_icon );
            planetViewTo = findViewById( R.id.planet_detail_tx_planetview );
            groupPlanet = findViewById( R.id.group_detail_tx_planet );
            groupAddress = findViewById( R.id.group_detail_tx_address );

            textPlanetToAddress = findViewById( R.id.text_detail_tx_planet_address );
            textPlanetName = findViewById( R.id.text_detail_tx_planet_name );
            textAddress = findViewById( R.id.text_detail_tx_address );
            textBalance = findViewById( R.id.text_detail_tx_amount );
            textStatus = findViewById( R.id.text_detail_tx_status );
            textAmount = findViewById( R.id.text_detail_tx_amount_ );
            textFee = findViewById( R.id.text_detail_tx_fee );
            textDate = findViewById( R.id.text_detail_tx_date );
            textTxID = findViewById( R.id.text_detail_tx_txhash );

        }
    }
}
